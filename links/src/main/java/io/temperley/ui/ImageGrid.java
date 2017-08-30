package io.temperley.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ImageRenderer;
import org.roadlessforest.ImageLabel;

import java.util.List;

@Theme("links")
public class ImageGrid {

    public final Grid<ImageLabel> grid = new Grid<>();

    private static String IMG_URL = "http://localhost:9999/seq/";

    private List<ImageLabel> imageLabelList;

    private ImageLabel getNext(ImageLabel imageLabel) {
        if (imageLabel == null) {
            return imageLabelList.get(0);
        }
        int idx = indexOf(imageLabel);
        return imageLabelList.get(idx + 1);
    }

    private int indexOf(ImageLabel imageLabel) {
        return imageLabelList.indexOf(imageLabel);
    }

    public void scrollTo(ImageLabel nextImage) {
        grid.scrollTo(indexOf(nextImage), ScrollDestination.MIDDLE);
    }

//    @Override

    public ImageGrid(List<ImageLabel> imageLabelList) {

        this.imageLabelList = imageLabelList;

        ListDataProvider<ImageLabel> dataProvider = DataProvider.ofCollection(imageLabelList);

        grid.setDataProvider(dataProvider);

        grid.addColumn(
                p -> new ExternalResource(IMG_URL + p.getHash()),
                new ImageRenderer()
        ).setCaption("Image");


        grid.addColumn(ImageLabel::getLabel).setCaption("Label");
        grid.addColumn(ImageLabel::getId).setCaption("ID");

        grid.setRowHeight(64);

    }

}
