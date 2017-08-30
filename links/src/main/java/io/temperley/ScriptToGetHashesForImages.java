package io.temperley;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.roadlessforest.ImageLabelDAO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by willtemperley@gmail.com on 26-May-17.
 */
@Deprecated
public class ScriptToGetHashesForImages {

//    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//
//        ImgHash imgHash = new ImgHash();
//        Map<String, String> pnghashToFile = new HashMap<>();
//
//        /**
//         * need to get
//         */
//        File file = new File("E:\\completeness_data\\64");
//        File[] files = file.listFiles();
//        for (File png : files) {
//
//            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
//            FileInputStream fileInputStream = new FileInputStream(png);
//            byteArrayOutputStream2.write(fileInputStream);
//            byte[] bytes1 = byteArrayOutputStream2.toByteArray();
//            String digest = imgHash.getDigest(bytes1);
//            pnghashToFile.put(digest, png.getName());
//        }
//
//        /*
//        need to map between image names and their hashes
//
//        need a png hash intermediate
//         */
//        ImagePreprocesser imagePreprocesser = new ImagePreprocesser(64);
//
//        ImageSeqFileAccess imageSeqFileAccess = new ImageSeqFileAccess();
//
//
//        Set<Map.Entry<String, int[]>> entries = null;// = imageSeqFileAccess.im.entrySet();
//
//
//
//        ImageLabelDAO imageLabelDAO = new ImageLabelDAO();
//
//        for (Map.Entry<String, int[]> entry : entries) {
//
//            int[] value = entry.getValue();
//            if (value.length > 4095) {
//
//                BufferedImage bufferedImage = imagePreprocesser.processTile(value);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//                ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
//
//                byte[] bytes = byteArrayOutputStream.toByteArray();
//
//                String pngDigest = imgHash.getDigest(bytes);
//
//                String fn = pnghashToFile.get(pngDigest);
//
//                String s = fn.split("\\.")[0];
//                System.out.println("fn = " + s);
//
//                imageLabelDAO.updateHash(Integer.valueOf(s), entry.getKey());
//
//            }
//
//        }
//
//
////        imageSeqFileAccess
//
//    }
}
