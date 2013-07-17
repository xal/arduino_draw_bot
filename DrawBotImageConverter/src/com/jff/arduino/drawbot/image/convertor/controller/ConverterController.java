package com.jff.arduino.drawbot.image.convertor.controller;

import com.jff.arduino.drawbot.image.convertor.model.GrayScaleConverter;
import com.jff.arduino.drawbot.image.convertor.model.GrayScaleImageBitmap;
import com.jff.arduino.drawbot.image.convertor.model.PaintLine;
import com.jff.arduino.drawbot.image.convertor.ui.ImageConvertPanel;
import com.jff.arduino.drawbot.image.convertor.ui.ImageInputPanel;
import com.jff.arduino.drawbot.image.convertor.ui.ImageOutputPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class ConverterController {


    private ImageConvertPanel imageConvertPanel;
    private ImageInputPanel imageInputPanel;
    private ImageOutputPanel imageOutputPanel;


    public ImageConvertPanel getImageConvertPanel() {
        return imageConvertPanel;
    }

    public void setImageConvertPanel(ImageConvertPanel imageConvertPanel) {
        this.imageConvertPanel = imageConvertPanel;
    }

    public ImageInputPanel getImageInputPanel() {
        return imageInputPanel;
    }

    public void setImageInputPanel(ImageInputPanel imageInputPanel) {
        this.imageInputPanel = imageInputPanel;
    }

    public ImageOutputPanel getImageOutputPanel() {
        return imageOutputPanel;
    }

    public void setImageOutputPanel(ImageOutputPanel imageOutputPanel) {
        this.imageOutputPanel = imageOutputPanel;
    }

    public void applyConvert() {
        Image normalImage = imageInputPanel.getNormalImage();
//
//
//
//        ImageData imageBitmapFromGrayScale = GrayScaleConverter.createImageBitmapFromGrayScale(grayScaleBitmap);
//

        Image greyed = new Image(Display.getCurrent(), normalImage, SWT.IMAGE_GRAY);


        Image scaledImage = new Image(Display.getCurrent(), greyed.getImageData().scaledTo(ImageOutputPanel.DRAW_WIDTH,
                ImageOutputPanel.DRAW_WIDTH));



        GrayScaleImageBitmap grayScaleBitmap = GrayScaleConverter.createGrayScaleBitmap(scaledImage);


        PaintLine paintLine = PaintLine.createPaintLine(grayScaleBitmap);

        //imageOutputPanel.changeOutputImageData(greyed);




        imageOutputPanel.changePaintLine(paintLine);

        greyed.dispose();
        scaledImage.dispose();


    }
}
