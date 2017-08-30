package org.roadlessforest.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.roadlessforest.ImageRenderer;
import org.roadlessforest.ImageSeqFileAccess;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Image renderer
 *
 * Created by willtemperley@gmail.com on 07-Apr-17.
 */
@WebServlet(urlPatterns = "/seq/*", name = "SeqFileServlet", asyncSupported = true)
public class SeqFileServlet extends HttpServlet {

    private static String PNG = "image/png";

    private ImageSeqFileAccess imageSeqFileAccess = new ImageSeqFileAccess();

    private ImageRenderer imageRenderer = new ImageRenderer(64);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType(PNG);

        OutputStream out = response.getOutputStream();

        //get the hash of the image requested
        String pathInfo = req.getPathInfo();
        String[] split = pathInfo.split("/");
        String hash = split[1];

        int[] imageData = imageSeqFileAccess.getImageData(hash);
        if (split.length == 2) {
            BufferedImage bufferedImage = imageRenderer.processTile(imageData);
            ImageIO.write(bufferedImage, "PNG", out);
        } else if (split.length > 2) {

            Integer[] keepers = new Integer[split.length - 2];
            for (int i = 2; i < split.length; i++) {
                keepers[i - 2] = Integer.parseInt(split[i]);
            }
            BufferedImage bufferedImage = imageRenderer.processTile(imageData, keepers);
            ImageIO.write(bufferedImage, "PNG", out);
        } else {
            throw new RuntimeException();
        }


        out.close();
    }
}
