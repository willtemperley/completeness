package org.roadlessforest.tile.tiff;


import org.geotools.resources.Arguments;
import org.openstreetmap.osmosis.hbase.xyz.ImageTiler;

/**
 * Created by willtemperley@gmail.com on 10-Mar-17.
 */
@Deprecated
public class TileToDirectory2 extends ImageTiler {

    public TileToDirectory2(int tileSize) {
        super(tileSize);
    }

    public static void main(String[] args) throws Exception {

        //GeoTools provides utility classes to parse command line arguments
        Arguments processedArgs = new Arguments(args);


        String inPath  = processedArgs.getRequiredString("-f");
        String outPath = processedArgs.getRequiredString("-o");
        int tileSize = processedArgs.getRequiredInteger("-t");

        TileToDirectory2 tiler = new TileToDirectory2(tileSize);

        TileImageWriter writer = new TileImageWriter(outPath);
        tiler.doTiling(inPath, writer);
    }

}
