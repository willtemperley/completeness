package org.roadlessforest.data;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.openstreetmap.osmosis.hbase.xyz.ImageTileWritable;

import java.io.IOException;


/**
 * Created by willtemperley@gmail.com on 10-Mar-17.
 */
public class ImageLibSeqFileWriter   {
    protected IntWritable k = new IntWritable();
    protected ImageTileWritable v = new ImageTileWritable();

    private final SequenceFile.Writer writer;

    public ImageLibSeqFileWriter(String outputDirectory) throws IOException {
        Configuration conf = new Configuration();
        SequenceFile.Writer.Option fileOption = SequenceFile.Writer.file(new Path(outputDirectory));

            this.writer = SequenceFile.createWriter(conf, fileOption,
                    SequenceFile.Writer.keyClass(IntWritable.class),
                    SequenceFile.Writer.valueClass(ImageTileWritable.class)
            );
    }

    public void append(int tileId, int[] image) throws IOException {
        System.out.println("i = " + tileId);
        k.set(tileId);
        v.setImage(image);
        writer.append(k, v);
    }

    public void close() throws IOException {
        writer.close();
    };
}
