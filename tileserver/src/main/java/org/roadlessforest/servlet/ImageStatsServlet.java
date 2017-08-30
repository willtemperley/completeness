package org.roadlessforest.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.roadlessforest.ImageRenderer;
import org.roadlessforest.ImageSeqFileAccess;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Image renderer
 *
 * Created by willtemperley@gmail.com on 07-Apr-17.
 */
@WebServlet(urlPatterns = "/stats/*", name = "ImageStatsServlet", asyncSupported = true)
public class ImageStatsServlet extends HttpServlet {

    private static String MIMETYPE = "application/json";

    private ImageSeqFileAccess imageSeqFileAccess = new ImageSeqFileAccess();

    Gson gson = new GsonBuilder().create();

    ImageRenderer imageRenderer = new ImageRenderer(64);



    /*
     * Want to know which classes are of interest in the training,
     */
    public Map<Integer, Integer> getHistogramAsMap(int[] ints) {

        int[] counts = new int[102];

        /*
        Build the histogram
         */
        for (int val : ints) {
            counts[val]++;
        }

        /*
        Put the counts into a map
         */
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0) {
                countMap.put(i, counts[i]);
            }
        }

        return countMap;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType(MIMETYPE);

        //get the hash of the image requested
        String pathInfo = req.getPathInfo();
        String[] split = pathInfo.split("/");
        String hash = split[1];

        String json;
        if (hash.equals("colourmap")) {
            json = gson.toJson(imageRenderer.getColourMap());
        } else {
            int[] imageData = imageSeqFileAccess.getImageData(hash);
            Map<Integer, Integer> countMap = getHistogramAsMap(imageData);
            json = gson.toJson(countMap);
        }


        OutputStream out = response.getOutputStream();
        PrintWriter printWriter = new PrintWriter(out);
        printWriter.write(json);
        printWriter.flush();

        out.close();
    }
}
