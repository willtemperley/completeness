package org.roadlessforest.tile;

import org.datavec.image.loader.NativeImageLoader;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Created by willtemperley@gmail.com on 12-May-17.
 */
public class TestReadImg {

    /**
     * Need to ensure the images are the same when they're read directly
     *
     * @throws IOException
     */
    @Test
    public void test() throws IOException {

        String testFileName = "E:\\completeness_data\\classify170503\\test\\1\\704.png";
        File testFile = new File(testFileName);

        BufferedImage read = ImageIO.read(testFile);

//        int i = 64 * 64;

//        int[] pixels = raster.getPixels(0, 0, 64, 64, new int[i]);

        NativeImageLoader nativeImageLoader = new NativeImageLoader(64, 64, 1);

        INDArray indArray = nativeImageLoader.asRowVector(testFile);

        System.out.println("indArray = " + indArray);
//        INDArray indArray;
//
//        dataBuffer


    }


}
