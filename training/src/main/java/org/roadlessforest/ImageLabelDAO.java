package org.roadlessforest;

import org.knowm.yank.Yank;

import java.util.List;

/**
 * Created by will on 16/04/2017.
 */
public class ImageLabelDAO {


    public void delete(ImageLabel entity) {

        String sql = "DELETE FROM completeness.labelled_image WHERE id = ?";
        Yank.execute(sql, new Object[]{entity.getId()});
    }

    void create(ImageLabel entity) {

        String sql = "INSERT INTO completeness.labelled_image (id, label) VALUES (?, ?)";
        Long insertId = Yank.insert(sql, new Object[]{entity.getId(), entity.getLabel()});

        /*
        Return the generated id
         */
        entity.setId(insertId);

    }


    public void update(ImageLabel labelledImage) {
        String sql = "UPDATE completeness.labelled_image SET label = ?, hash = ?, training = ? WHERE id = ?";

        Yank.execute(sql, new Object[]{labelledImage.getLabel(), labelledImage.getHash(), labelledImage.isTraining(), labelledImage.getId()});

    }

    public List<ImageLabel> listClassified(boolean training) {

        return Yank.queryBeanList("SELECT id, label, hash, training FROM completeness.labelled_image " +
                "where label > -1 and training = ? ORDER BY id", ImageLabel.class, new Object[]{training});

    }

    public List<ImageLabel> list() {

        return Yank.queryBeanList("SELECT id, label, hash, training FROM completeness.labelled_image ORDER BY id", ImageLabel.class, new Object[]{});
    }

}
