package org.roadlessforest.class256;

import org.geotools.resources.Arguments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

/**
 * Created by willtemperley@gmail.com on 21-Apr-17.
 */
@Deprecated
public class TestTrainSplit {

    public static void main(String[] args) throws IOException {

//        Arguments processedArgs = new Arguments(args);
//
//        String inputPath = processedArgs.getRequiredString("-f");
//        int nClasses = processedArgs.getRequiredInteger("-n");
//
//        File inFolder = new File(inputPath);
//
//        File classified = new File(inFolder, "classified");
//        File train = new File(inFolder, "train");
//        File test = new File(inFolder, "test");
//
//        Random random = new Random(System.currentTimeMillis());
//
//        for (int i = 0; i < nClasses; i++) {
//
//            File classifiedN = new File(classified, i + "");
//            File[] list = classifiedN.listFiles();
//            File trainN = new File(train, i + "");
//            File testN = new File(test, i + "");
//
//            for (File f : list) {
//                System.out.println("f.getAbsolutePath() = " + f.getAbsolutePath());
//                float v = random.nextFloat();
//                System.out.println("v = " + v);
//
//                File whereTo;
//                if (v < 0.25) {
//                    whereTo = new File(testN, f.getName());
//                } else {
//                    whereTo = new File(trainN, f.getName());
//                }
//                Files.copy(f.toPath(), whereTo.toPath());
//
//            }
//        }

//        for (String s : list) {
//            File f = new File(outFolder, s);
//            File f1 = new File(folder1, s);
//            try {
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
