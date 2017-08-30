package org.roadlessforest.data;

import org.geotools.resources.Arguments;
import org.roadlessforest.model.ImageTiler;

import java.io.File;

/**
 * Created by willtemperley@gmail.com on 23-May-17.
 */
public class ImageLibCreateCmd extends ImageTiler {

    public ImageLibCreateCmd(int tileSize) {
        super(tileSize);
    }

    public static void main(String[] args) throws Exception {

        //GeoTools provides utility classes to parse command line arguments
        Arguments processedArgs = new Arguments(args);

        String inPath  = processedArgs.getRequiredString("-f");
        String outPath = processedArgs.getRequiredString("-o");
        int tileSize = processedArgs.getRequiredInteger("-t");

        ImageLibSeqFileWriter imageLibSeqFileWriter = new ImageLibSeqFileWriter(outPath);

        ImageLibCreateCmd imageLibCreateCmd = new ImageLibCreateCmd(tileSize);

        imageLibCreateCmd.doTiling(inPath, imageLibSeqFileWriter);

    }

}
