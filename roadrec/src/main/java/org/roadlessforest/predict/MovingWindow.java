package org.roadlessforest.predict;

import com.esri.core.geometry.Envelope2D;
import org.openstreetmap.osmosis.hbase.xyz.TileWriter;
import org.openstreetmap.osmosis.hbase.xyz.geotiff.GeoTiffReader;
import org.openstreetmap.osmosis.hbase.xyz.geotiff.ImageMetadata;
import xyz.wgs84.TileKey;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Created by willtemperley@gmail.com on 03-May-17.
 */
public abstract class MovingWindow {

    private final int targetTileSize;

    public MovingWindow(int tileSize) {
        this.targetTileSize = tileSize;
    }

}