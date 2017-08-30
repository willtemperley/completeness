package org.roadlessforest.class256;

import org.geotools.resources.Arguments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by willtemperley@gmail.com on 21-Apr-17.
 */
@Deprecated
public class CopyToLabelledFolders {

    public static void main(String[] args) {

        Arguments processedArgs = new Arguments(args);

        String patternPath  = processedArgs.getRequiredString("-p");
        String outputPath  = processedArgs.getRequiredString("-o");

        File inFolder = new File(patternPath);
        File outFolder = new File(outputPath);


        int nClass = 4;
        for (int i = 0; i < nClass; i++) {
            String pattern = "test/" + i;
            copyFiles(pattern, inFolder, outFolder);
            pattern = "train/" + i;
            copyFiles(pattern, inFolder, outFolder);
        }

    }

    private static void copyFiles(String pattern, File inFolder, File outFolder) {
        File folder = new File(inFolder, pattern);
        File folder1 = new File(outFolder, pattern);
        String[] list = folder.list();
        for (String s : list) {
            File f = new File(outFolder, s);
            File f1 = new File(folder1, s);
            try {
                Files.copy(f.toPath(), f1.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("f.getAbsolutePath() = " + f.getAbsolutePath());
        }
    }
}
