package org.roadlessforest.model;

import org.roadlessforest.ResourceAccess;

import java.awt.image.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by willtemperley@gmail.com on 20-Apr-17.
 *
 *  All image hacks are kept in the same place - so the same hacks are used in training and production
 *
 */
@Deprecated
public class ImagePreprocesser  {


    private final int tileSize;
    Map<Integer, Integer>  integerMap = new HashMap<>();

    public ImagePreprocesser(int tileSize) {

        this.tileSize = tileSize;

        List<String> linesFromFile = ResourceAccess.getLinesFromFile("classification.clr");
        for (String colourMapEntry : linesFromFile) {
            String[] split = colourMapEntry.split(" ");
            int k = Integer.parseInt(split[0]);
            int r = Integer.parseInt(split[1]);
            int g = Integer.parseInt(split[2]);
            int b = Integer.parseInt(split[3]);

            integerMap.put(k, getPackedRGBA(255, r, g, b));
        }
    }

    private int getPackedRGBA(int a, int r, int g, int b) {
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }

    public BufferedImage processTile(int[] matrix) throws IOException {

        int nZeroes = 0;
        int[] counts = new int[95];

        int width = tileSize;
        int height = tileSize;

        int[] packed = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {

//            packed[i] = getPackedRGBA(255, matrix[i], 0,0);
            int val = matrix[i];
            counts[val] ++;

            if (val == 11) {
                nZeroes++;
                packed[i] = getPackedRGBA(255, 0,0,0);
            } else {
                packed[i] = integerMap.get(val);
            }
        }

        if (((width * height) - nZeroes) < 10) {
            return null;
        }

//        System.out.println("*** I ***");
//        for (int i = 0; i < counts.length; i++) {
//            if (counts[i] > 0) {
//                System.out.println("counts["+i+"] = " + counts[i]);
//            }
//        }

        DataBufferInt buffer = new DataBufferInt(packed, packed.length);

        int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000}; // ARGB (yes, ARGB, as the masks are R, G, B, A always) order
        WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, bandMasks, null);

        ColorModel cm = ColorModel.getRGBdefault();
        BufferedImage bufferedImage = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);


        return bufferedImage;
//        tile(bufferedImage);


    }


}
