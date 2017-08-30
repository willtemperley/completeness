package org.roadlessforest.domain;

import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;
import org.roadlessforest.ImageLabel;
import org.roadlessforest.ImageLayer;
import org.roadlessforest.ImageLayerDAO;

import java.util.List;
import java.util.Properties;


/**
 * Created by willtemperley@gmail.com on 20-Jun-17.
 */
public class TestYank {

    public static void main(String[] args) {
        DataAccessConfig.init();

        ImageLayerDAO imageLayerDAO = new ImageLayerDAO();

        List<ImageLayer> list = imageLayerDAO.list();

        for (ImageLayer imageLayer : list) {
            System.out.println("imageLayer = " + imageLayer);
        }

    }
}
