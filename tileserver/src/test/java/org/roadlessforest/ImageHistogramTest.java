package org.roadlessforest;

import org.junit.Test;
import org.roadlessforest.stats.ImageHistogram;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by willtemperley@gmail.com on 14-Jun-17.
 */
public class ImageHistogramTest {

    /**
     *
     */
    @Test
    public void testHistogram(){

        ImageHistogram imageHistogram = new ImageHistogram(10);

        int[] ints = new int[]{1,1,1,5,5,5,3,4,1,1};

        imageHistogram.doCounting(ints);

        int[] values = imageHistogram.getValues();

        Arrays.sort(values);
        assertTrue(Arrays.equals(values, new int[]{1,3,4,5}));

        assertTrue(imageHistogram.getCount(1) == 5);
        assertTrue(imageHistogram.getCount(2) == 0);
        assertTrue(imageHistogram.getCount(3) == 1);
        assertTrue(imageHistogram.getCount(4) == 1);
        assertTrue(imageHistogram.getCount(5) == 3);

        //see if re-use works
        int[] ints2 = new int[]{1,1,1,1,1,1,1,1,1,2};
        imageHistogram.doCounting(ints2);
        values = imageHistogram.getValues();
        Arrays.sort(values);
        assertTrue(Arrays.equals(values, new int[]{1,2}));

        assertTrue(imageHistogram.getCount(1) == 9);
        assertTrue(imageHistogram.getCount(2) == 1);

    }

}
