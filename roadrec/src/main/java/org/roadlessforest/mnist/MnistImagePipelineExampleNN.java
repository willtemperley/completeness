package org.roadlessforest.mnist;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class MnistImagePipelineExampleNN {

    private static Logger logger = LoggerFactory.getLogger(MnistImagePipelineExampleNN.class);

    public static void main(String[] args) throws IOException {

        int height = 28;
        int width = 28;
        int channels = 1;
        int rngseed = 1234;
        Random random = new Random(rngseed);

        int batchSize = 128;
        int outputNum = 10;
        int epoch = 15;

        File training = new File("E:/completeness/roadrec/mnist_png/training");
        File testing = new File("E:/completeness/roadrec/mnist_png/testing");
        if (! training.exists()) {
            throw new RuntimeException("missing training file");
        }
        if (! testing.exists()) {
            throw new RuntimeException("missing training file");
        }

        FileSplit train = new FileSplit(training, NativeImageLoader.ALLOWED_FORMATS, random);
        FileSplit test = new FileSplit(testing, NativeImageLoader.ALLOWED_FORMATS, random);

        ParentPathLabelGenerator parentPathLabelGenerator = new ParentPathLabelGenerator();

        ImageRecordReader imageRecordReader = new ImageRecordReader(height, width, channels, parentPathLabelGenerator);

        imageRecordReader.initialize(train);

        //imageRecordReader.setListeners(new LogRecordListener());

        DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(imageRecordReader,
                batchSize, 1, outputNum);

        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        dataSetIterator.setPreProcessor(scaler);

        //model
        logger.info("** build model **");

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngseed)
                .iterations(1)
                .learningRate(0.006)
                .updater(Updater.NESTEROVS)
                .momentum(0.9)
                .regularization(true).l2(1e-4)
                .list()
                    .layer(0, new DenseLayer.Builder()
                            .nIn(height * width)
                            .nOut(100)
                            .activation("relu")
                            .weightInit(WeightInit.XAVIER)
                            .build()
                    ).layer(1, new OutputLayer.Builder()
                        .nIn(100)
                        .nOut(outputNum)
                        .activation("softmax")
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false).backprop(true)
                .setInputType(InputType.convolutional(height, width, channels))
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(10));

        logger.info("*** Training ***");
        for (int i = 0; i < epoch; i++) {
            model.fit(dataSetIterator);
        }

        logger.info("*** Evaluate ***");
        imageRecordReader.reset();

        imageRecordReader.initialize(test);

        DataSetIterator testIter = new RecordReaderDataSetIterator(imageRecordReader,
                batchSize, 1, outputNum);
        scaler.fit(dataSetIterator);
        testIter.setPreProcessor(scaler);

        Evaluation evaluation = new Evaluation(outputNum);

        while (testIter.hasNext()) {
            DataSet next = testIter.next();
            INDArray outut = model.output(next.getFeatureMatrix());
            evaluation.eval(next.getLabels(), outut);
        }

        logger.info(evaluation.stats());


    }

}