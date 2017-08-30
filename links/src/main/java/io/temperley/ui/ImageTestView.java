package io.temperley.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by willtemperley@gmail.com on 31-May-17.
 */
public class ImageTestView extends VerticalLayout {

    public ImageTestView() {
        setSizeFull();
        Button button = new Button("Go to Main View",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
//                        navigator.navigateTo(MAINVIEW);
                    }
                });
        addComponent(button);
        setComponentAlignment(button, Alignment.MIDDLE_CENTER);
    }

}
