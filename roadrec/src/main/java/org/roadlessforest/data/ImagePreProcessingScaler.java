package org.roadlessforest.data;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.serializer.NormalizerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copied from nd4j
 *
 */

public class ImagePreProcessingScaler implements DataNormalization {
    private static final Logger log = LoggerFactory.getLogger(org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler.class);
    private double minRange;
    private double maxRange;
    private double maxPixelVal;
    private int maxBits;

    public ImagePreProcessingScaler() {
        this(0.0D, 1.0D, 8);
    }

    public ImagePreProcessingScaler(double a, double b) {
        this(a, b, 8);
    }

    public ImagePreProcessingScaler(double a, double b, int maxBits) {
        this.maxPixelVal = Math.pow(2.0D, (double) maxBits) - 1.0D;
        this.minRange = a;
        this.maxRange = b;
    }

    public void fit(DataSet dataSet) {
    }

    public void fit(DataSetIterator iterator) {
    }

    public void preProcess(DataSet toPreProcess) {
        INDArray features = toPreProcess.getFeatures();
        this.preProcess(features);
    }

    public void preProcess(INDArray features) {
        features.divi(Double.valueOf(this.maxPixelVal));
        if (this.maxRange - this.minRange != 1.0D) {
            features.muli(Double.valueOf(this.maxRange - this.minRange));
        }

        if (this.minRange != 0.0D) {
            features.addi(Double.valueOf(this.minRange));
        }

    }

    public void transform(DataSet toPreProcess) {
        this.preProcess(toPreProcess);
    }

    public void transform(INDArray features) {
        this.preProcess(features);
    }

    public void transform(INDArray features, INDArray featuresMask) {
        this.transform(features);
    }

    public void transformLabel(INDArray label) {
    }

    public void transformLabel(INDArray labels, INDArray labelsMask) {
        this.transformLabel(labels);
    }

    public void revert(DataSet toRevert) {
        this.revertFeatures(toRevert.getFeatures());
    }

    public NormalizerType getType() {
        return NormalizerType.IMAGE_MIN_MAX;
    }

    public void revertFeatures(INDArray features) {
        if (this.minRange != 0.0D) {
            features.subi(Double.valueOf(this.minRange));
        }

        if (this.maxRange - this.minRange != 1.0D) {
            features.divi(Double.valueOf(this.maxRange - this.minRange));
        }

        features.muli(Double.valueOf(this.maxPixelVal));
    }

    public void revertFeatures(INDArray features, INDArray featuresMask) {
        this.revertFeatures(features);
    }

    public void revertLabels(INDArray labels) {
    }

    public void revertLabels(INDArray labels, INDArray labelsMask) {
        this.revertLabels(labels);
    }

    public void fitLabel(boolean fitLabels) {
        if (fitLabels) {
            log.warn("Labels fitting not currently supported for ImagePreProcessingScaler. Labels will not be modified");
        }

    }

    public boolean isFitLabel() {
        return false;
    }
}