package org.roadlessforest.data.reader;

import com.google.common.annotations.VisibleForTesting;
import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.BaseRecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.datavec.common.RecordConverter;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.util.NDArrayUtil;
import org.roadlessforest.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a Hadoop sequence file containing labelled images.
 *
 * Image labels are held separately in a database (for some reason).
 *
 * Created by willtemperley@gmail.com on 29-May-17.
 */
public class SeqFileRecordReader extends BaseRecordReader {

    private ImageLabel imageLabel;

    @VisibleForTesting
    public static void main(String[] args) {

        ImageRenderer imageRenderer = new ImageRenderer(64);
        int z = 0;
        try {
            while (test.hasNext()) {
                BufferedImage bufferedImage = imageRenderer.processTile(test.nextValue());
                ImageIO.write(bufferedImage, "PNG", new FileOutputStream(new File("E:/tmp/recordreader/test/" + z + ".png")));
                z++;
            }
            while (train.hasNext()) {
                BufferedImage bufferedImage = imageRenderer.processTile(train.nextValue());
                ImageIO.write(bufferedImage, "PNG", new FileOutputStream(new File("E:/tmp/recordreader/train/" + z + ".png")));
                z++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ImageSeqFileAccess imageSeqFileAccess = new ImageSeqFileAccess();

    private ImageLabelDAO imageLabelDAO = new ImageLabelDAO();

    private ImageLayerDAO imageLayerDAO = new ImageLayerDAO();

    private List<ImageLabel> imageLabels;

    private int idx = 0;

    public static SeqFileRecordReader test = new SeqFileRecordReader(false);

    public static SeqFileRecordReader train = new SeqFileRecordReader(true);

    /**
     *
     * @param isTraining which image set to use.
     */
    public SeqFileRecordReader(boolean isTraining) {

        imageLabels = imageLabelDAO.listClassified(isTraining);

    }

    @Override
    public void initialize(InputSplit split) throws IOException, InterruptedException {
//        this.inputSplit = split;
    }

    @Override
    public void initialize(Configuration conf, InputSplit split) throws IOException, InterruptedException {
//        initialize(inputSplit);
    }

    /**
     * Water and forest are set to zero
     *
     * @param image the image to mask
     */
    public static void applyMasks(int[] image) {
        for (int i = 0; i < image.length; i++) {
            if (image[i] == 71 || image[i] == 72 || image[i] == 11) {
                image[i] = 0;
            }
        }
    }

    private void filter(int[] image, int[] toKeep) {
        int[] keepers = new int[120];
//        keepers[71]
        for (int i : toKeep) {
            keepers[i] = 1;
        }

        for (int i = 0; i < image.length; i++) {
            int val = image[i];
            if (keepers[val] == 0) {
                image[i] = 0;
//            } else {
//                image[i] = 94;
            }
        }
    }

    private int[] nextValue() {

        imageLabel = imageLabels.get(idx);
        idx++;

        List<ImageLayer> layers = imageLayerDAO.getLayers(imageLabel);

        String hash = imageLabel.getHash();

        int[] imageData = imageSeqFileAccess.getImageData(hash);

        applyMasks(imageData);

        if (imageLabel.getLabel() == 1) {
            int[] toKeep = new int[layers.size()];
            for (int i = 0; i < layers.size(); i++) {
                toKeep[i] = layers.get(i).getPixVal();
            }
            filter(imageData, toKeep);
        }

        return imageData;
    }


    @Override
    public List<Writable> next() {

        int[] imageData = nextValue();

        INDArray indArray = NDArrayUtil.toNDArray(imageData);
        List<Writable> ret = RecordConverter.toRecord(indArray);
        ret.add(new IntWritable(imageLabel.getLabel().intValue()));

        return ret;
    }


    @Override
    public boolean hasNext() {
        return idx < imageLabels.size();
    }

    @Override
    public List<String> getLabels() {
        List<String> labels = new ArrayList<>();

        for (ImageLabel imageLabel: imageLabels) {
            labels.add("" + imageLabel.getLabel());
        }

        return labels;
    }

    @Override
    public void reset() {
        idx = 0;
    }

    @Override
    public List<Writable> record(URI uri, DataInputStream dataInputStream) throws IOException {
        return null;
    }

    @Override
    public Record nextRecord() {
        return null;
    }

    @Override
    public Record loadFromMetaData(RecordMetaData recordMetaData) throws IOException {
        return null;
    }

    @Override
    public List<Record> loadFromMetaData(List<RecordMetaData> recordMetaDatas) throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void setConf(Configuration conf) {

    }

    @Override
    public Configuration getConf() {
        return null;
    }
}

