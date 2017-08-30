package org.roadlessforest;

import org.knowm.yank.Yank;

import java.util.List;

/**
 * Created by willtemperley@gmail.com on 22-Jun-17.
 */
public class ImageLayerDAO {

    public void delete(ImageLayer entity) {

        String sql = "DELETE FROM completeness.image_layer WHERE id = ?";
        Yank.execute(sql, new Object[]{entity.getId()});
    }

    public void create(ImageLayer entity) {

        String sql = "INSERT INTO completeness.image_layer (id, pix_val, layer_type) VALUES (?, ?, ?)";
        Long insertId = Yank.insert(sql, new Object[]{entity.getId(), entity.getPixVal(), entity.getLayerType()});

        /*
        Return the generated id
         */
        entity.setId(insertId);

    }


    public void update(ImageLayer labelledImage) {
        String sql = "UPDATE completeness.image_layer SET pix_val = ?, labelled_image_id = ?, layer_type = ? WHERE id = ?";

        Yank.execute(sql, new Object[]{labelledImage.getPixVal(), labelledImage.getImageId(), labelledImage.getLayerType(), labelledImage.getId()});

    }


    public List<ImageLayer> list() {

        return Yank.queryBeanList("SELECT id, layer_type, pix_val  FROM completeness.image_layer ORDER BY id", ImageLayer.class, new Object[]{});
    }

    public List<ImageLayer> getLayers(ImageLabel imageLabel) {

        return Yank.queryBeanList("SELECT id, labelled_image_id, layer_type, pix_val FROM completeness.image_layer WHERE labelled_image_id = ? ORDER BY id", ImageLayer.class, new Object[]{imageLabel.getId()});
    }
}
