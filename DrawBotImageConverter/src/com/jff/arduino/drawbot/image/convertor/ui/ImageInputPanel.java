package com.jff.arduino.drawbot.image.convertor.ui;

import com.jff.arduino.drawbot.image.convertor.controller.ConverterController;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import javax.xml.bind.helpers.AbstractMarshallerImpl;

public class ImageInputPanel extends Composite {
    private final ConverterController controller;
    private Button buttonLoad;
    private Label imageView;

    private static int IMAGE_VIEW_WIDTH = 300;
    private static int IMAGE_VIEW_HEIGHT = 500;
    private Image scaledImage;
    private Image normalImage;

    public ImageInputPanel(Composite parent, ConverterController controller) {
        super(parent, SWT.NONE);
        this.controller = controller;
        init();
    }

    private void init() {


        Layout layout = new GridLayout(1, false);
        this.setLayout(layout);



        imageView = new Label(this, SWT.NONE);

        buttonLoad = new Button(this, SWT.NONE);
        buttonLoad.setText("Load");

        imageView.setSize(IMAGE_VIEW_WIDTH, IMAGE_VIEW_HEIGHT);




        buttonLoad.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {


                FileDialog dialog = new FileDialog(ImageInputPanel.this.getShell(), SWT.OPEN);
                String file = dialog.open();
                if (file != null) {


                    String path = file;
                    loadImage(path);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        loadImage("./test_files/google.png");

        showScaledImage();

    }

    private void loadImage(String path) {

        Image image = null;

        final Device display = getDisplay();

        image = null;
        try {
            image = new Image(display, path);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        if (image != null) {

            if (normalImage!= null)
                normalImage.dispose();
            normalImage = image;
        }

        showScaledImage();
    }

    private void showScaledImage() {

        Device display = getDisplay();
        if (scaledImage!= null) {
            scaledImage.dispose();
        }

        scaledImage = new Image(display, normalImage.getImageData().scaledTo(IMAGE_VIEW_WIDTH, IMAGE_VIEW_HEIGHT));
        imageView.setImage(scaledImage);

    }

    public Image getNormalImage() {
        return normalImage;
    }

    public void setNormalImage(Image normalImage) {
        this.normalImage = normalImage;
    }
}
