package io.temperley.cmd;


import org.roadlessforest.ImageLabel;
import org.roadlessforest.ImageLabelDAO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;


/**
 * Created by willtemperley@gmail.com on 21-Apr-17.
 */
public class CopyLabelledImages {

    public static void main(String[] args) throws IOException {

        ImageLabelDAO imageLabelDAO = new ImageLabelDAO();
        List<ImageLabel> imageLabels = imageLabelDAO.listClassified(true);

        Arguments processedArgs = new Arguments(args);

        String inputPath  = processedArgs.getRequiredString("-f");
        String outputPath  = processedArgs.getRequiredString("-o");

        File inFolder = new File(inputPath);

        File testFolder = new File(outputPath, "test");
        File trainFolder = new File(outputPath, "train");

        Random random = new Random(System.currentTimeMillis());

        for (ImageLabel imageLabel : imageLabels) {
            Long label = imageLabel.getLabel();
//            if (label == 2) {
//                label = 0L;
//            }
            Long id = imageLabel.getId();

            File inFile = new File(inFolder, id + ".png");
            System.out.println("inFile = " + inFile.getAbsolutePath());

            if (!inFile.exists()) {
                throw new RuntimeException();
            }

            float v = random.nextFloat();
            File whereTo = v < 0.25 ? testFolder: trainFolder;
            whereTo = new File(whereTo, label + "");
            if (!whereTo.exists()) {
                whereTo.createNewFile();
            }

            File outFile = new File(whereTo, inFile.getName());

            System.out.println("outFile = " + outFile.getAbsolutePath());
            Files.copy(inFile.toPath(), outFile.toPath());

        }

    }


    private static void copyFiles(String pattern, File inFolder, File outFolder) {
    }
}
