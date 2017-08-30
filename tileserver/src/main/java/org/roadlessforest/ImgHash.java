package org.roadlessforest;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by willtemperley@gmail.com on 26-May-17.
 */
public class ImgHash {

    private MessageDigest md = MessageDigest.getInstance("SHA-256");

    public ImgHash() throws NoSuchAlgorithmException {

    }

    public static void main(String[] args)  {

        try {
            ImgHash imgHash = new ImgHash();



        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public String getDigest(byte[] img) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        md.update(img);
        byte[] digest = md.digest();
        return new BigInteger(1, digest).toString(16);
    }

}
