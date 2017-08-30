package io.temperley.ui.view;

import io.temperley.ui.ImageGrid;
import io.temperley.ui.ImagePanel;
import io.temperley.ui.MultiColumnLayout;
import org.roadlessforest.ImageLabel;
import org.roadlessforest.ImageLabelDAO;

import java.util.List;
import java.util.Optional;

/**
 * Created by willtemperley@gmail.com on 31-May-17.
 */
public class ClassificationView extends MultiColumnLayout {

    private ImageLabelDAO imageLabelDAO = new ImageLabelDAO();

    private static String IMG_URL = "http://localhost:9999/";

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

    public ClassificationView() {
        super(2);

        imageLabelList = imageLabelDAO.list();
        ImageGrid imageGrid = new ImageGrid(imageLabelList);
        this.addToColumn(1, imageGrid.grid);
        ImagePanel imagePanel = new ImagePanel(IMG_URL);
        this.addToColumn(0, imagePanel);

        imageGrid.grid.addSelectionListener(l -> {
            Optional<ImageLabel> firstSelectedItem = l.getFirstSelectedItem();
            if (firstSelectedItem.isPresent()) {
                ImageLabel imageLabel = firstSelectedItem.get();
                imagePanel.setNext(imageLabel);
            }
        });

        imagePanel.setListener((eventCode, imageLabel) -> {
            //set the new data
            imageLabel.setLabel((long) eventCode);

            //Update db and refresh ui
            imageLabelDAO.update(imageLabel);
            imageGrid.grid.getDataProvider().refreshItem(imageLabel);

            ImageLabel nextImage = getNext(imageLabel);
            imageGrid.scrollTo(nextImage);
            imageGrid.grid.select(nextImage);
            return nextImage;
        });

        imagePanel.setNext(getNext(null));
    }
}
