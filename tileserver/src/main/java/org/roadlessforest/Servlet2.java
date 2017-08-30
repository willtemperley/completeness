package org.roadlessforest;

import com.esri.core.geometry.BinaryScanCallback;
import com.esri.core.geometry.Envelope2D;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.TileRasterizer;
import xyz.mercator.MercatorTile;
import xyz.mercator.MercatorTileCalculator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by willtemperley@gmail.com on 07-Apr-17.
 */
@WebServlet(urlPatterns = "/tiles/*", name = "Servlet2", asyncSupported = true)
@Deprecated
public class Servlet2 extends HttpServlet {

    private static String PNG = "image/png";

    MercatorTileCalculator mercatorTileCalculator = new MercatorTileCalculator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType(PNG);

        OutputStream out = response.getOutputStream();

        String pathInfo = req.getPathInfo();
        String[] zxy = pathInfo.split("/");
        int z = Integer.valueOf(zxy[1]);
        int x = Integer.valueOf(zxy[2]);
        int y = Integer.valueOf(zxy[3]);

        MercatorTile mercatorTile = new MercatorTile(x, y, z);

        Envelope2D tileEnvelope = mercatorTileCalculator.getTileEnvelope(mercatorTile);

        Polygon polygon = new Polygon();
        polygon.startPath(0, 0);
        polygon.lineTo(10, 10);
        polygon.lineTo(10, 0);
        polygon.lineTo(0, 0);
//            polygon.startPath(tileEnvelope.xmin, tileEnvelope.ymin);
//            polygon.lineTo(tileEnvelope.xmax, tileEnvelope.ymax);
//            polygon.lineTo(tileEnvelope.xmax, tileEnvelope.ymin);
//            polygon.lineTo(tileEnvelope.xmin, tileEnvelope.ymin);
        polygon.closeAllPaths();

        TileRasterizer tileRasterizer = new TileRasterizer(mercatorTile, new BinaryScanCallback(256, 256));

        tileRasterizer.rasterizePolygon(polygon);

//            out.write(TextToGraphics.renderText(x + "," +  y + "," +  z));
        out.write(tileRasterizer.getImage());

        out.close();
    }
}
