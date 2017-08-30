package org.roadlessforest.model;

import com.esri.core.geometry.Envelope2D;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.parameter.DefaultParameterDescriptor;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.resources.Arguments;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.util.NDArrayUtil;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.openstreetmap.osmosis.hbase.xyz.geotiff.GeoTiffReader;
import org.openstreetmap.osmosis.hbase.xyz.geotiff.ImageMetadata;
import org.roadlessforest.stats.ImageHistogram;
import org.roadlessforest.data.reader.SeqFileRecordReader;

import javax.imageio.ImageWriteParam;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by willtemperley@gmail.com on 10-May-17.
 */
public class ModelRunner {

    //todo: Make parallel;

    public static final int tileSize = 64;
    private static ImagePreprocesser imagePreprocesser;
    private static WritableRaster raster;

    public static void main(String[] args) throws IOException {

        //Load the model
        Model64 model64 = Model64.instance;
        final MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(model64.getFileLocation());

        //GeoTools provides utility classes to parse command line arguments
        Arguments processedArgs = new Arguments(args);

        String inPath = processedArgs.getRequiredString("-f");
        String outPath = processedArgs.getRequiredString("-o");

        runPrediction(inPath, outPath, model);

        System.exit(0);

//        File directory = new File("E:\\aaa");
////
//        File[] files = directory.listFiles();
//
//        M64 m64 = new M64(model);
////
//        for (int i = 0; i < files.length; i++) {
//            File file = files[i];
//            BufferedImage read = ImageIO.read(file);
//
//
//            int predictedFromFile = m64.predict(file);
//            int predictedFromBuff = m64.predict(read);
//            if (predictedFromBuff != predictedFromFile) {
//                System.out.println("predictedFromFile = " + predictedFromFile);
//                System.out.println("predictedFromBuff = " + predictedFromBuff);
//            }
////            }
//        }

    }

    public static class M64 {

        private final MultiLayerNetwork model;

        public M64(MultiLayerNetwork multiLayerNetwork) {
            this.model = multiLayerNetwork;
        }

        final DataNormalization scaler = getDataNormalization();

        final NativeImageLoader nativeImageLoader = new NativeImageLoader(64, 64, 1);

        public int predict(BufferedImage bufferedImage) {

            try {
                INDArray indArray = nativeImageLoader.asRowVector(bufferedImage);
                return predict(indArray);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        public int predict(File file) {

            try {
                INDArray indArray = nativeImageLoader.asRowVector(file);
                return predict(indArray);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        private int predict(INDArray indArray) {
            scaler.transform(indArray);
            int[] predict = model.predict(indArray);
            return predict[0];
        }

    }

    private static DataNormalization getDataNormalization() {
        return new ImagePreProcessingScaler(0, 1);
    }


    public static void runPrediction(String inputFilePath, String outputFilePath, MultiLayerNetwork model) throws IOException {

        GeoTiffReader geoTiffReader = new GeoTiffReader();
        GeoTiffReader.ReferencedImage referencedImage = geoTiffReader.readGeotiffFromFile(new File(inputFilePath));

        ImageMetadata metadata = referencedImage.getMetadata();
        int w = metadata.getWidth();
        int h = metadata.getHeight();
        RenderedImage renderedImage = referencedImage.getRenderedImage();

        BufferedImage outputImage = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = outputImage.getRaster();

        Raster data = renderedImage.getData();
        final DataNormalization scaler = getDataNormalization();

        int stride = 10;
        int[] IMAGE = new int[tileSize * tileSize];
        int[] LAYER = new int[tileSize * tileSize];
        int[] BURN = new int[tileSize * tileSize];

        ImageHistogram imageHistogram = new ImageHistogram(95);

        for (int i = 0; i < w - tileSize; i += stride) {
            System.out.println("i = " + i);
            for (int j = 0; j < h - tileSize; j += stride) {

                //fixme move out of loop?
                data.getPixels(i, j, tileSize, tileSize, IMAGE);

                imageHistogram.doCounting(IMAGE);

                SeqFileRecordReader.applyMasks(IMAGE);
//                //todo figure out how to apply the same preprocessing as the model
//                //todo try different layers (and combinations of layers);
//

                Random random = new Random(System.currentTimeMillis());

                int[] values = imageHistogram.getValues();
                for (int itr = 0; itr < 5; itr++) {

                    int[] toTest = new int[5];
                    for (int k = 0; k < 5; k++) {
                        toTest[k] = values[random.nextInt(values.length)];
                    }

                    imageHistogram.setValuesToTest(toTest);

//                    int count = imageHistogram.getCount(v);
                    for (int l = 0; l < LAYER.length; l++) {

                        int pixVal = IMAGE[l];

                        boolean burn = imageHistogram.test(pixVal);

                        if (burn) {
                            LAYER[l] = pixVal;
                        } else {
                            LAYER[l] = 0;
                        }


                    }

                    INDArray indArray = NDArrayUtil.toNDArray(LAYER);
                    scaler.transform(indArray);

                    int[] predict = model.predict(indArray);
                    if (predict[0] == 1) {
                        for (int z = 0; z < BURN.length; z++) {
                            if (LAYER[z] > 0) {
                                BURN[z] = 1;
                            } else {
                                BURN[z] = 0;
                            }
                        }
//                    System.out.println("predict = " + predict[0]);
                        burn(raster, tileSize, i, j, BURN);
                    }
                }
            }
        }

        write(outputFilePath, outputImage, metadata);
    }

    /**
     * @param raster
     * @param tileSize
     * @param w
     * @param h
     * @param image
     */
    private static void burn(WritableRaster raster, int tileSize, int w, int h, int[] image) {
        int[] current = new int[tileSize * tileSize];
        raster.getPixels(w, h, tileSize, tileSize, current);
        for (int x = 0; x < current.length; x++) {
            if (image[x] != 0) {
                current[x] += 1;
            }
        }
        raster.setPixels(w, h, tileSize, tileSize, current);
    }

    public static void write(String outputImgFN, BufferedImage bigImage, ImageMetadata imageMetadata) throws IOException {

        //getting the write parameters
        GeoTiffWriteParams wp = new GeoTiffWriteParams();
        GeoTiffFormat format = new GeoTiffFormat();

        //setting compression to LZW
        wp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        wp.setCompressionType("LZW");
        wp.setCompressionQuality(1.0F);

        ParameterValueGroup params = format.getWriteParameters();
        params.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(wp);
        List<GeneralParameterValue> x = params.values();

        Envelope2D envelope2D = imageMetadata.getEnvelope2D();

        org.geotools.geometry.Envelope2D bbox = new org.geotools.geometry.Envelope2D(DefaultGeographicCRS.WGS84, envelope2D.xmin, envelope2D.ymin, envelope2D.getWidth(), envelope2D.getHeight());
        GridCoverage2D coverage = new GridCoverageFactory().create("tif", bigImage, bbox);
        GeoTiffWriter gtw = new GeoTiffWriter(new File(outputImgFN));

        DefaultParameterDescriptor<Boolean> retainAxes = GeoTiffFormat.RETAIN_AXES_ORDER;
        ParameterValue<Boolean> retainAxesValue = retainAxes.createValue();
        retainAxesValue.setValue(true);

        x.add(retainAxesValue);
        gtw.write(coverage, x.toArray(new GeneralParameterValue[2]));

    }

}
