package org.roadlessforest;

/**
 * Created by willtemperley@gmail.com on 12-Jun-17.
 */
public class ImageLayer implements HasPixVal {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long imageId;

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    private Integer pixVal;

    @Override
    public Integer getPixVal() {
        return pixVal;
    }

    @Override
    public void setPixVal(Integer pixVal) {
        this.pixVal = pixVal;
    }

    private Integer layerType;

    public Integer getLayerType() {
        return layerType;
    }

    public void setLayerType(Integer layerType) {
        this.layerType = layerType;
    }
}
