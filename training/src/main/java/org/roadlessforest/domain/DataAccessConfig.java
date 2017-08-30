package org.roadlessforest.domain;

import org.knowm.yank.PropertiesUtils;
import org.knowm.yank.Yank;

import java.util.Properties;

/**
 * Created by willtemperley@gmail.com on 22-Jun-17.
 */
public class DataAccessConfig {

    static Properties dbProps = PropertiesUtils.getPropertiesFromClasspath("pg.properties");


    public static void init() {
        Yank.setupDefaultConnectionPool(dbProps);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("releasing.");
                Yank.releaseAllConnectionPools();
            }
        }));
    }
}
