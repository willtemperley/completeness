package org.roadlessforest.mnist;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.listener.impl.LogRecordListener;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MnistImagePipelineExample {

    private static Logger logger = LoggerFactory.getLogger(MnistImagePipelineExample.class);

    public static void main(String[] args) throws IOException {

        int height = 28;
        int width = 28;
        int channels = 1;
        int rngseed = 1234;
        Random random = new Random(rngseed);

        int outputNum = 10;
        int batchSize = 1;


        File training = new File("mnist_png/training");
        File testing = new File("mnist_png/testing");

        FileSplit train = new FileSplit(training, NativeImageLoader.ALLOWED_FORMATS, random);
        FileSplit test = new FileSplit(testing, NativeImageLoader.ALLOWED_FORMATS, random);

        ParentPathLabelGenerator parentPathLabelGenerator = new ParentPathLabelGenerator();

        ImageRecordReader imageRecordReader = new ImageRecordReader(height, width, channels, parentPathLabelGenerator);

        imageRecordReader.initialize(train);

        imageRecordReader.setListeners(new LogRecordListener());

        DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(imageRecordReader,
                batchSize, 1, outputNum);

        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        dataSetIterator.setPreProcessor(scaler);

        for (int i = 1; i < 2; i++) {
            DataSet ds = dataSetIterator.next(i);
            System.out.println(ds);
            List<String> labels = dataSetIterator.getLabels();
            System.out.println(labels);
        }





    }

}