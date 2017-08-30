package io.temperley.ui;

import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import org.roadlessforest.HasPixVal;
import org.roadlessforest.ImageLabel;
import org.roadlessforest.ImageLayer;
import org.roadlessforest.ImageLayerDAO;

import java.util.*;

/**
 *
 * Created by willtemperley@gmail.com on 26-Apr-17.
 */
public class ImagePanel extends CustomComponent implements Action.Handler {

    //fixme


    private Set<ImagePixels> selectedItems;
    private TileServerApi tileServerApi;
    private final Map<Integer, String> colourmap;
    private final Grid<ImagePixels> imagePixelsGrid;

    ImageLayerDAO imageLayerDAO = new ImageLayerDAO();

    public static interface ClassificationEventListener {

        public ImageLabel classify(int i, ImageLabel imageLabel);
    }

    private ClassificationEventListener listener;
    private final ImageTile imageTile;
    private final ImageTile imageTile2;
    private final String serverURL;
    // Define and create user interface components
    Panel panel = new Panel("Classify");
    HorizontalLayout formlayout = new HorizontalLayout();
    VerticalLayout buttons = new VerticalLayout();

    // Create buttons and define their listener methods.
    Button Minus1 = new Button("-1", event -> eventHandler(-1));
    Button Noise = new Button("0", event -> eventHandler(0));
    Button Road1 = new Button("1", event -> eventHandler(1));
    Button Road2 = new Button("2", event -> eventHandler(2));

    private ImageLabel imageLabel;

    Button saveLayers = new Button("L", event -> {

        if (selectedItems != null) {
            for (ImagePixels selectedItem : selectedItems) {
                ImageLayer imageLayer = new ImageLayer();
                imageLayer.setLayerType(0); //todo
                imageLayer.setImageId(imageLabel.getId());
                imageLayer.setPixVal(selectedItem.getPixVal());
                imageLayerDAO.create(imageLayer);
            }
        }

    });

    // Have the unmodified Enter key cause an event
    Action actionQ = new ShortcutAction("Q",
            ShortcutAction.KeyCode.Q, null);

    // Have the C key modified with Alt cause an event
    Action actionA = new ShortcutAction("A",
            ShortcutAction.KeyCode.A, null);

    Action actionS = new ShortcutAction("S",
            ShortcutAction.KeyCode.S, null);

    Action actionD = new ShortcutAction("D",
            ShortcutAction.KeyCode.D, null);

    Action actionZ = new ShortcutAction("Z",
            ShortcutAction.KeyCode.Z, null);


    public void setListener(ClassificationEventListener listener) {
        this.listener = listener;
    }

    public static class ImagePixels implements HasPixVal {
        private String hexColour;
        private Integer count;
        private Integer pixelValue;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getHexColour() {
            return "<div style='background-color:" + hexColour + "'>&nbsp;&nbsp;&nbsp;</div>";
        }

        public void setHexColour(String hexColour) {
            this.hexColour = hexColour;
        }

        public void setPixVal(Integer pixelValue) {
            this.pixelValue = pixelValue;
        }

        public Integer getPixVal() {
            return pixelValue;
        }
    }

    public ImagePanel(String imgURL) {

        this.serverURL = imgURL;
        this.tileServerApi = new TileServerApi(serverURL + "stats/");
        this.colourmap = tileServerApi.getColourMap();


        setCompositionRoot(panel);

        imageTile = new ImageTile();
        imageTile2 = new ImageTile();
        formlayout.addComponent(imageTile);
        formlayout.addComponent(imageTile2);


        panel.setContent(formlayout);
        formlayout.addComponent(buttons);
        buttons.addComponent(Minus1);
        buttons.addComponent(Noise);
        buttons.addComponent(Road1);
        buttons.addComponent(Road2);
        buttons.addComponent(saveLayers);

        // Set this object as the action handler
        panel.addActionHandler(this);

        imagePixelsGrid = new Grid<>();
        imagePixelsGrid.addColumn(ImagePixels::getPixVal).setCaption("pixel value");
        imagePixelsGrid.addColumn(ImagePixels::getCount).setCaption("count");
        imagePixelsGrid.addColumn(ImagePixels::getHexColour, new HtmlRenderer()).setCaption("colour");
        imagePixelsGrid.setWidth(400, Unit.PIXELS);
        imagePixelsGrid.setHeight(400, Unit.PIXELS);

        imagePixelsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        imagePixelsGrid.addSelectionListener(e -> {
            Set<ImagePixels> allSelectedItems = e.getAllSelectedItems();
            this.selectedItems = allSelectedItems;

            setTrainingPixels(allSelectedItems);
        });

        formlayout.addComponent(imagePixelsGrid);
    }

    /**
     *
     *
     * @param trainingPixels a list of those pixels of interest
     */
    private void setTrainingPixels(Collection<? extends HasPixVal> trainingPixels) {
        StringJoiner joiner = new StringJoiner("/");

        for (HasPixVal pixVal : trainingPixels) {
            joiner.add(pixVal.getPixVal().toString());
            String v = "/" + joiner.toString();
            ExternalResource externalResource = new ExternalResource(serverURL + "seq/" + imageLabel.getHash() + v);
            imageTile2.setSource(externalResource);
        }
    }

    public void setNext(ImageLabel imageLabel) {

        List<ImageLayer> layers = imageLayerDAO.getLayers(imageLabel);

        ExternalResource externalResource = new ExternalResource(serverURL + "seq/" + imageLabel.getHash());
        this.imageLabel = imageLabel;
        this.imageTile.setSource(externalResource);

        Map<Integer, Integer> imageStats
                = tileServerApi.getImageStats(imageLabel.getHash());

        /*
         * Create a list of all the pixel values and their counts
         */
        List<ImagePixels> imagePixelsList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> integerIntegerEntry : imageStats.entrySet()) {

            ImagePixels x = new ImagePixels();
            Integer value = integerIntegerEntry.getValue();
            x.setCount(value);
            Integer key = integerIntegerEntry.getKey();
            x.setPixVal(key);
            String hexColour = colourmap.get(key);
            x.setHexColour(hexColour);
            imagePixelsList.add(x);
        }

        imagePixelsGrid.setItems(imagePixelsList);
        for (ImagePixels imagePixels : imagePixelsList) {
            for (HasPixVal layer : layers) {
                if (layer.getPixVal().equals(imagePixels.getPixVal())) {
                    imagePixelsGrid.select(imagePixels);
                }
            }
        }

        setTrainingPixels(imagePixelsList);

    }


    /**
     * Retrieve actions for a specific component. This method
     * will be called for each object that has a handler; in
     * this example just for login panel. The returned action
     * list might as well be static list.
     */
    public Action[] getActions(Object target, Object sender) {
        return new Action[]{actionQ, actionA, actionZ};//, actionS, actionD};
    }

    /**
     * Handle actions received from keyboard. This simply directs
     * the actions to the same listener methods that are called
     * with ButtonClick events.
     */
    public void handleAction(Action action, Object sender, Object target) {

        Map<Integer, Integer> actionMap = new HashMap<>();
        actionMap.put(ShortcutAction.KeyCode.Q, 0);
        actionMap.put(ShortcutAction.KeyCode.A, 1);
        actionMap.put(ShortcutAction.KeyCode.Z, 2);
        actionMap.put(ShortcutAction.KeyCode.D, -1);

        if (action instanceof ShortcutAction) {
            ShortcutAction shortcutAction = (ShortcutAction) action;
            System.out.println("shortcutAction = " + shortcutAction.getKeyCode());

            int keyCode = shortcutAction.getKeyCode();
            Integer integer = actionMap.get(keyCode);
            eventHandler(integer);

        }

//        if (action == actionQ) {
//            eventHandler(0);
//            Noise.focus();
//        }
//        if (action == actionA) {
//            eventHandler(1);
//            Road1.focus();
//        }
//        if (action == actionS) {
//            eventHandler(2);
//            Road2.focus();
//        }
//        if (action == actionD) {
//            eventHandler(3);
//            Road3.focus();
//        }
//        if (action == actionZ) {
//            eventHandler(4);
//            Other.focus();
//        }
    }

    public void eventHandler(int eventCode) {

        System.out.println("event = " + eventCode);

        ImageLabel next = listener.classify(eventCode, imageLabel);
        setNext(next);

    }

}
