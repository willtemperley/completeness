package io.temperley.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import java.lang.reflect.Array;

/**
 * A standard pattern: create a twin column layout with full size
 *
 * Created by will on 16/04/2017.
 */
public class MultiColumnLayout extends VerticalLayout {

    private final VerticalLayout[] verticalLayouts;

    public MultiColumnLayout(int nCols) {
        setSizeFull();

        verticalLayouts = new VerticalLayout[nCols];
        for (int i = 0; i < nCols; i++) {
            VerticalLayout layout = new VerticalLayout();
            verticalLayouts[i] = layout;
            this.addComponent(layout);
            layout.setSizeFull();
        }
    }

    public void addToColumn(int idx, Component component) {
        verticalLayouts[idx].addComponent(component);
    }
}
