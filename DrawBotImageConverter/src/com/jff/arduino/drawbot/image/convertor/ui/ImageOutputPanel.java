package com.jff.arduino.drawbot.image.convertor.ui;

import com.jff.arduino.drawbot.image.convertor.controller.ConverterController;
import com.jff.arduino.drawbot.image.convertor.model.PaintLine;
import com.jff.arduino.drawbot.image.convertor.model.Point2D;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.*;
import java.util.List;

public class ImageOutputPanel extends Composite {
    private final ConverterController controller;
    private Button buttonSave;
    private Label imageView;

    private static int IMAGE_VIEW_WIDTH = 300;
    private static int IMAGE_VIEW_HEIGHT = 500;

    private Image normalImage;
    private Image scaledImage;
    private Canvas canvas;
    private PaintLine paintLine;

    public ImageOutputPanel(Composite parent, ConverterController controller) {
        super(parent, SWT.NONE);
        this.controller = controller;
        init();
    }

    private void init() {


        Layout layout = new GridLayout(1, false);
        this.setLayout(layout);

        imageView = new Label(this, SWT.NONE);

        GridData layoutData = new GridData(IMAGE_VIEW_WIDTH, IMAGE_VIEW_HEIGHT);


        canvas = new Canvas(this, SWT.NONE);
        canvas.setLayoutData(layoutData);

        canvas.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

        canvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent paintEvent) {

                GC gc = paintEvent.gc;

                if(paintLine != null) {

                    gc.setForeground(new Color(Display.getCurrent(), 0,0,0));

                    List<Point2D> points = paintLine.getPoints();

                    int prevX = points.get(0).x;
                    int prevY = points.get(0).y;

                    for(Point2D point2d : points) {

                        int currentX = point2d.x;
                        int currentY = point2d.y;


                        gc.drawLine(prevX, prevY, currentX, currentY);

                        prevX = currentX;
                        prevY = currentY;
                    }
                }


            }
        });

//        imageView.setLayoutData(layoutData);
//        imageView.setSize(IMAGE_VIEW_WIDTH, IMAGE_VIEW_HEIGHT);


        buttonSave = new Button(this, SWT.NONE);
        buttonSave.setText("Save");






        buttonSave.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {




            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

    }


    public void changeOutputImageData(Image imageBitmapFromGrayScale) {

        if(normalImage != null) {
            normalImage.dispose();
        }

        normalImage = imageBitmapFromGrayScale;

        showScaledImage();

    }


    private void showScaledImage() {

        Device display = getDisplay();
        if (scaledImage!= null) {
            scaledImage.dispose();
        }

        scaledImage = new Image(display, normalImage.getImageData().scaledTo(IMAGE_VIEW_WIDTH, IMAGE_VIEW_HEIGHT));
        imageView.setImage(scaledImage);


        this.layout();

    }

    public void changePaintLine(PaintLine paintLine) {

        this.paintLine = paintLine;

        canvas.redraw();
    }
}
