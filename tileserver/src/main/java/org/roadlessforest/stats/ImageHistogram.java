package org.roadlessforest.stats;

/**
 * Just counts the
 *
 * Created by willtemperley@gmail.com on 14-Jun-17.
 */
public class ImageHistogram {

    /**
     * @param nClasses the number of possible values
     */
    public ImageHistogram(int nClasses) {
        counts = new int[nClasses];
        toTest = new boolean[nClasses];
    }

    private final int[] counts;
    private final boolean[] toTest;

    public void setValuesToTest(int[] vals) {
        //reset
        for (int i = 0; i < toTest.length; i++) {
            toTest[i] = false;
        }
        for (int i = 0; i < vals.length; i++) {
            toTest[vals[i]] = true;
        }
    }

    public boolean test(int testVal) {
        return toTest[testVal];
    }

    public void doCounting(int[] img) {

        //reset counts
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }

        for (int val : img) {
            counts[val]++;
        }
    }

    public int[] getValues() {
        int nVals = 0;
        for (int count : counts) {
            if (count > 0) {
                nVals++;
            }
        }

        int[] vals = new int[nVals];
        int j = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0) {
                vals[j] = i;
                j++;
            }
        }
        return vals;

    }

    public int getCount(int i) {
        return counts[i];
    }
}
