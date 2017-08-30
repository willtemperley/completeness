package org.roadlessforest.model;

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
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.roadlessforest.data.reader.SeqFileRecordReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class TileClassifierSeqFile {

    private static Logger logger = LoggerFactory.getLogger(TileClassifierSeqFile.class);

    public static void main(String[] args) throws IOException {

        int height = 64;
        int width = 64;
        int channels = 1;
        int rngseed = 41234451;// (int) System.currentTimeMillis();
        Random random = new Random(rngseed);

//        int batchSize = 128;
        int batchSize = 64;
        int outputNum = 3;  //n classes
        int epoch = 220;

        /*
        Read from sequence files
         */
        SeqFileRecordReader seqFileRecordReaderTrain = SeqFileRecordReader.train;
        SeqFileRecordReader seqFileRecordReaderTest = SeqFileRecordReader.test;

        DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(seqFileRecordReaderTrain, batchSize, 1, outputNum);

        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        dataSetIterator.setPreProcessor(scaler);

        //model
        logger.info("** build model **");

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngseed)
                .iterations(1)
                .learningRate(0.0003)
                .updater(Updater.NESTEROVS)
                .momentum(0.9)
//                .regularization(true)
//                .l2(1e-4)
                .list()
                    .layer(0, new DenseLayer.Builder()
                            .nIn(height * width)
                            .nOut(200)
                            .activation(Activation.TANH)
                            .weightInit(WeightInit.XAVIER)
                            .build()
                    ).layer(1, new OutputLayer.Builder()
                        .nIn(200)
                        .nOut(outputNum)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.SIGMOID_UNIFORM)
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
//        imageRecordReader.reset();

//        imageRecordReader.initialize(test);

        DataSetIterator testIter = new RecordReaderDataSetIterator(seqFileRecordReaderTest,
                batchSize, 1, outputNum);
        scaler.fit(dataSetIterator);
        testIter.setPreProcessor(scaler);

        Evaluation evaluation = new Evaluation(outputNum);

        while (testIter.hasNext()) {
            DataSet next = testIter.next();
            INDArray output = model.output(next.getFeatureMatrix());
            evaluation.eval(next.getLabels(), output);
        }

        logger.info(evaluation.stats());


        //Save the model
        File locationToSave = new File("t64.zip");      //Where to save the network. Note: the file is in .zip format - can be opened externally
        boolean saveUpdater = true;                     //Updater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this if you want to train your network more in the future
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);

//        model.predict()

    }

}