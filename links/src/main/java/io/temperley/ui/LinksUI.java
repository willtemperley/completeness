package io.temperley.ui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import io.temperley.ui.view.ClassificationView;
import org.roadlessforest.domain.DataAccessConfig;

/**
 * This UI is the application entry point. Noise UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("links")
public class LinksUI extends UI {

    static {
        DataAccessConfig.init();
    }

    ClassificationView classificationView = new ClassificationView();

    public Button pushStateTest() {

        Button button = new Button("Go to page 1");
        button.addClickListener(e -> {
            // URL will change to .../page1
            MultiColumnLayout layout = new MultiColumnLayout(4);
            layout.addToColumn(0, new Button("asdf"));
            UI.getCurrent().setContent(layout);

            Page.getCurrent().pushState("images");
        });

        Page.getCurrent().addPopStateListener(event -> {
            String uri = event.getUri();
            System.out.println("uri = " + uri);
        });

        return button;
    }


    @Override
    protected void init(VaadinRequest vaadinRequest) {

        setContent(classificationView);
//        SplineUpdatingEachSecond splineUpdatingEachSecond = new SplineUpdatingEachSecond();
//        setContent(splineUpdatingEachSecond.getWrappedComponent());


    }


    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = LinksUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

}
