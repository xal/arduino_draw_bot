package com.jff.arduino.drawbot.image.convertor.model;

import java.util.ArrayList;
import java.util.List;

public class PaintLine {

    private List<Point2D> points = new ArrayList<Point2D>();

    public List<Point2D> getPoints() {
        return points;
    }


    public static PaintLine createPaintLine(GrayScaleImageBitmap grayScaleImageBitmap) {

        PaintLine paintLine = new PaintLine();

        int width = grayScaleImageBitmap.getWidth();
        int height = grayScaleImageBitmap.getHeight();





        return paintLine;
    }

}
