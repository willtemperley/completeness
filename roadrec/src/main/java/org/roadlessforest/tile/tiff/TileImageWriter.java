package org.roadlessforest.tile.tiff;

import org.openstreetmap.osmosis.hbase.xyz.TileWriter;
import org.roadlessforest.ResourceAccess;
import xyz.wgs84.TileKey;

import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by willtemperley@gmail.com on 20-Apr-17.
 */
@Deprecated
public class TileImageWriter implements TileWriter {

    private final File folder;
    int imgidx = 0;

    Map<Integer, Integer>  integerMap = new HashMap<>();

    public TileImageWriter(String outPath) {

        List<String> linesFromFile = ResourceAccess.getLinesFromFile("remap");

        for (String remap : linesFromFile) {

            if (remap.equals("")) continue;
            if (remap.startsWith("#")) continue;

            String[] split = remap.split(" ");
            int k = Integer.parseInt(split[0]);
            int v = Integer.parseInt(split[1]);
//            int g = Integer.parseInt(split[2]);
//            int b = Integer.parseInt(split[3]);

            integerMap.put(k, v);
        }

        folder = new File(outPath);
    }


    @Override
    public void append(TileKey key, int[] matrix) throws IOException {

        int nZeroes = 0;

        int width = key.getWidth();
        int height = key.getHeight();

        int[] packed = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
//            packed[i] = getPackedRGBA(255, matrix[i], 0,0);
            int val = matrix[i];
            if (val == 11) {
                nZeroes++;
//                packed[i] = getPackedRGBA(255, 0,0,0);
            } else {
                packed[i] = integerMap.get(val);
            }
        }

        if (((width * height) - nZeroes) < 10) {
            return;
        }

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster1 = bufferedImage.getRaster();
        raster1.setPixels(0,0, width, height, packed);


//        DataBufferInt buffer = new DataBufferInt(packed, packed.length);

//        int[] bandMasks = {0xFF0000, 0xFF00, 0xFF, 0xFF000000}; // ARGB (yes, ARGB, as the masks are R, G, B, A always) order
//        WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, bandMasks, null);


//        ColorModel cm = ColorModel.getRGBdefault();
//        BufferedImage bufferedImage = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);

//        byte[] image1 = BinaryImage.getImage(image, width, height);

        File out = new File(folder, imgidx + ".tif");

        ImageIO.write(bufferedImage, "TIFF", out);
        imgidx++;

    }

    @Override
    public void close() throws IOException {

    }
}
