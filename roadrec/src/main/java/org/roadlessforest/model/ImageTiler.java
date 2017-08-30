package org.roadlessforest.model;

import com.esri.core.geometry.Envelope2D;
import org.openstreetmap.osmosis.hbase.xyz.geotiff.GeoTiffReader;
import org.openstreetmap.osmosis.hbase.xyz.geotiff.ImageMetadata;
import org.roadlessforest.data.ImageLibSeqFileWriter;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by willtemperley@gmail.com on 23-May-17.
 */
public abstract class ImageTiler {
    private final int targetTileSize;
    int imgIdx = 0;

    public ImageTiler(int tileSize) {
        this.targetTileSize = tileSize;
    }

    public void doTiling(String inputFilePath, ImageLibSeqFileWriter writer) throws IOException {
        GeoTiffReader geoTiffReader = new GeoTiffReader();
        GeoTiffReader.ReferencedImage referencedImage = geoTiffReader.readGeotiffFromFile(new File(inputFilePath));
        ImageMetadata metadata = referencedImage.getMetadata();
        int w = metadata.getWidth();
        int h = metadata.getHeight();
        Envelope2D imgEnv = metadata.getEnvelope2D();
        double geographicPixHeight = (imgEnv.ymax - imgEnv.ymin) / (double)h;
        RenderedImage renderedImage = referencedImage.getRenderedImage();
        double nTilesX = Math.ceil((double)w / (double)this.targetTileSize);
        double nTilesY = Math.ceil((double)h / (double)this.targetTileSize);
        Raster data = renderedImage.getData();

        for(int i = 0; (double)i < nTilesX; ++i) {
            int sizeX = this.targetTileSize;
            if((i + 1) * this.targetTileSize > w) {
                sizeX = w - i * this.targetTileSize;
            }

            int tileOrigX = i * sizeX;
            double geoOffsetLeft = (double)(i * this.targetTileSize) * geographicPixHeight;

            for(int j = 0; (double)j < nTilesY; ++j) {
                int sizeY = this.targetTileSize;
                if((j + 1) * this.targetTileSize > h) {
                    sizeY = h - j * this.targetTileSize;
                }

                int tileOrigY = j * sizeY;
                int[] IMAGE = new int[sizeX * sizeY];
                data.getPixels(tileOrigX, tileOrigY, sizeX, sizeY, IMAGE);


                {//FIXME this is all a nasty hack
                    int nZeroes = 0;
                    for (int pix : IMAGE) {
//            packed[i] = getPackedRGBA(255, matrix[i], 0,0);
                        if (pix == 11) {
                            nZeroes++;
                        }
                    }

                    if (((targetTileSize * targetTileSize) - nZeroes) > 10) {
                        writer.append(imgIdx, IMAGE);
                        imgIdx++;
                    }
                }
            }
        }

        writer.close();
    }
}
