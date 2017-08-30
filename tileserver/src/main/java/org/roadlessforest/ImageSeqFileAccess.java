package org.roadlessforest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.openstreetmap.osmosis.hbase.xyz.ImageTileWritable;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by willtemperley@gmail.com on 26-Apr-17.
 *
 * Given
 */
public class ImageSeqFileAccess {

    private Map<String, int[]> im = new HashMap<>(10000);

    public int[] getImageData(String h) {
        return im.get(h);
    }

    public ImageSeqFileAccess() {

        try {
            ImgHash imgHash = new ImgHash();
            SequenceFile.Reader reader = new SequenceFile.Reader(new Configuration(), SequenceFile.Reader.file(new Path("E:/completeness_data/image_seq_64")));

            IntWritable key = new IntWritable();
            ImageTileWritable val = new ImageTileWritable();

            while (reader.next(key, val)) {
                String digest = imgHash.getDigest(val.get());
                im.put(digest, val.getImage());
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

}
