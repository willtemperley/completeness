package org.roadlessforest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by willtemperley@gmail.com on 05-Apr-17.
 */
public class ResourceAccess {

    public static List<String> getLinesFromFile(String fn) {
        try {
            return Files.readAllLines(getFile(fn).toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e); //just work, or explode
        }
    }

    /**
     * Reads entire file to a byte array and decodes
     *
     * @param fn
     * @return
     */
    public static String readFileToString(String fn, Class<?> clazz) {

        InputStream inputStream = getResource(fn, clazz);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private static InputStream getResource(String fn, Class<?> aClass) {
        ClassLoader classLoader = aClass.getClassLoader();
        return classLoader.getResourceAsStream(fn);
    }

    private static File getFile(String fn) {
        ClassLoader classLoader = xyz.ResourceAccess.class.getClassLoader();
        return new File(classLoader.getResource(fn).getPath());
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args) throws IOException {
        String s = readFileToString("test.txt", xyz.ResourceAccess.class);
        System.out.println("s = " + s);
    }

}
