package org.roadlessforest;

import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by willtemperley@gmail.com on 20-Apr-17.
 *
 */
public class ImageRenderer {

    private final int tileSize;
    Map<Integer, Integer> pixValToRGBA = new HashMap<>();

    private Map<Integer, String> colourMap = new HashMap<>();

    public Map<Integer, String> getColourMap() {
        return colourMap;
    }

    private static File getFile(String fn) {
        ClassLoader classLoader = ImageRenderer.class.getClassLoader();
        return new File(classLoader.getResource(fn).getPath());
    }

    public ImageRenderer(int tileSize) {

        this.tileSize = tileSize;


        List<String> linesFromFile = null;
        try {
            linesFromFile = Files.readAllLines(getFile("classification.clr").toPath(), Charset.defaultCharset());
            for (String colourMapEntry : linesFromFile) {
                String[] split = colourMapEntry.split(" ");
                int k = Integer.parseInt(split[0]);
                int r = Integer.parseInt(split[1]);
                int g = Integer.parseInt(split[2]);
                int b = Integer.parseInt(split[3]);

                String hex = String.format("#%02x%02x%02x", r, g, b);
                colourMap.put(k, hex);

                pixValToRGBA.put(k, getPackedRGBA(255, r, g, b));
            }
            //Zeros are opaque black.
            pixValToRGBA.put(0, getPackedRGBA(255, 0, 0, 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getPackedRGBA(int a, int r, int g, int b) {
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }


    public BufferedImage processTile(int[] matrix) throws IOException {


        int width = tileSize;
        int height = tileSize;

        int[] packed = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {

            int val = matrix[i];
            Integer integer = pixValToRGBA.get(val);
            packed[i] = integer;
        }

        DataBufferInt buffer = new DataBufferInt(packed, packed.length);

        int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000}; // ARGB (yes, ARGB, as the masks are R, G, B, A always) order
        WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, bandMasks, null);

        ColorModel cm = ColorModel.getRGBdefault();
        BufferedImage bufferedImage = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);

        return bufferedImage;

    }

    public BufferedImage processTile(int[] matrix, Integer ... ints) throws IOException {

        int[] keepers = new int[120];
        for (Integer anInt : ints) {
            keepers[anInt] = 1;
        }

        int width = tileSize;
        int height = tileSize;

        int[] packed = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {

            int val = matrix[i];
            if (keepers[val] == 1) {
                packed[i] = pixValToRGBA.get(val);
            } else {
                packed[i] = 0xFF000000;
            }
        }

        DataBufferInt buffer = new DataBufferInt(packed, packed.length);

        int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000}; // ARGB (yes, ARGB, as the masks are R, G, B, A always) order
        WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, bandMasks, null);

        ColorModel cm = ColorModel.getRGBdefault();
        BufferedImage bufferedImage = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);

        return bufferedImage;

    }


}
