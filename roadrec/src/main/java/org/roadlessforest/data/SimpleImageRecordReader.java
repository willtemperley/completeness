package org.roadlessforest.data;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.datavec.common.RecordConverter;
import org.datavec.image.recordreader.BaseImageRecordReader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.util.NDArrayUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Created by willtemperley@gmail.com on 22-May-17.
 */
@Deprecated
public class SimpleImageRecordReader extends BaseImageRecordReader {

    public SimpleImageRecordReader(int tileSize) {
        super();
        this.height = tileSize;
        this.width = tileSize;
        this.channels = 1;
        this.labelGenerator = new ParentPathLabelGenerator();
        this.appendLabel = true;
    }

    @Override
    public List<Writable> next() {
        if (iter != null) {
            List<Writable> ret;
            File image = iter.next();
            currentFile = image;

            if (image.isDirectory())
                return next();
            try {
                invokeListeners(image);
//                INDArray row = imageLoader.asMatrix(image);
                BufferedImage read = ImageIO.read(image);
                int[] ints = new int[height * width * 4];
                read.getData().getPixels(0, 0, width, height, ints);
                INDArray indArray = NDArrayUtil.toNDArray(ints);

                ret = RecordConverter.toRecord(indArray);
                if (appendLabel)
                    ret.add(new IntWritable(labels.indexOf(getLabel(image.getPath()))));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return ret;
        } else if (record != null) {
            hitImage = true;
            invokeListeners(record);
            return record;
        }
        throw new IllegalStateException("No more elements");
    }

}
