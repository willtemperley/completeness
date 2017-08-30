package org.roadlessforest.tile.png;


import org.geotools.resources.Arguments;
import org.openstreetmap.osmosis.hbase.xyz.ImageTiler;
import org.openstreetmap.osmosis.hbase.xyz.cmd.TileToSequenceFile;
import org.roadlessforest.model.ImagePreprocesser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by willtemperley@gmail.com on 10-Mar-17.
 */
public class TileToDirectory extends ImageTiler {

    public TileToDirectory(int tileSize) {
        super(tileSize);
    }

    public static void main(String[] args) throws Exception {

        //GeoTools provides utility classes to parse command line arguments
        Arguments processedArgs = new Arguments(args);

        String inPath  = processedArgs.getRequiredString("-f");
        String outPath = processedArgs.getRequiredString("-o");
        final File folder = new File(outPath);
        int tileSize = processedArgs.getRequiredInteger("-t");

        TileToDirectory tiler = new TileToDirectory(tileSize);

        TileToSequenceFile tileToSequenceFile = new TileToSequenceFile();


//        ImagePreprocesser writer = new ImagePreprocesser(outPath);
//        ImagePreprocesser writer = new ImagePreprocesser() {
//            int imgidx = 0;
//            @Override
//            public int getTileSize() {
//                return 64;
//            }
//
////            @Override
//            public void tile(BufferedImage bufferedImage) {
//
//                File out = new File(folder, imgidx + ".png");
//
//                try {
//                    ImageIO.write(bufferedImage, "PNG", out);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imgidx++;
//            }
//        };
//        tiler.doTiling(inPath, writer); //fixme
    }

}
