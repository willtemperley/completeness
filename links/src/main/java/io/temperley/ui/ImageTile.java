package io.temperley.ui;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.CssLayout;
import org.vaadin.alump.scaleimage.ScaleImage;

/**
 * Created by willtemperley@gmail.com on 26-Apr-17.
 */
public class ImageTile extends CssLayout {

    ScaleImage scaleImage = new ScaleImage();

    public ImageTile() {
        scaleImage.setWidth(128, Unit.PIXELS);
        scaleImage.setHeight(128, Unit.PIXELS);
        scaleImage.setStyleName("tile-image");

        setStyleName("tile");
        setWidth(200, Unit.PIXELS);
        setHeight(200, Unit.PIXELS);
        addComponent(scaleImage);
    }

    public void setSource(ExternalResource resource) {
        scaleImage.setSource(resource);
    }

}
