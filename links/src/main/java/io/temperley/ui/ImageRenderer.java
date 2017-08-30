package io.temperley.ui;

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
 * All image hacks are kept in the same place - so the same hacks are used in training and production
 */
public class ImageRenderer {

    private final int tileSize;
    Map<Integer, Integer> integerMap = new HashMap<>();

    public Map<Integer, Integer> getIntegerMap() {
        return integerMap;
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

                integerMap.put(k, getPackedRGBA(255, r, g, b));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getPackedRGBA(int a, int r, int g, int b) {
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }


}
