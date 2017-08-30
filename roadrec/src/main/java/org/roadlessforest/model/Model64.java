package org.roadlessforest.model;

/**
 * Created by willtemperley@gmail.com on 12-May-17.
 */
public class Model64 implements ISerializedModel {

    static Model64 instance = new Model64();

    @Override
    public String getFileLocation() {
        return "t64.zip";
    }

    @Override
    public int getTileSize() {
        return 64;
    }

    @Override
    public int getSeed() {
        return 41234451;
    }
}
