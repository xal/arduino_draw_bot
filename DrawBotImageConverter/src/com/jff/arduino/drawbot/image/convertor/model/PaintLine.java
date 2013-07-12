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


        boolean reverse = false;

        double diagonalLength = (float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));

        double rStep = 5;
        double thetaStep = 0.001;

        for (double i = 0; i < diagonalLength; i += rStep) {
            double r = i;


            if (reverse) {
                for (double j = 0; j <= Math.PI / 2; j += thetaStep) {

                    double theta = j;

                    addPointIntoLine(paintLine, r, theta, width, height);


                }
            } else {

                for (double j = Math.PI / 2; j >= 0; j -= thetaStep) {

                    double theta = j;

                    addPointIntoLine(paintLine, r, theta, width, height);


                }


            }
            reverse = !reverse;


        }


        return paintLine;
    }

    private static void addPointIntoLine(PaintLine paintLine, double r, double theta, int width, int height) {
        PolarPoint2D polarPoint2D = new PolarPoint2D(r, theta);

        Point2D point2D = PolarPoint2D.toCartesian(polarPoint2D);


        if (point2D.x < width && point2D.y < height) {

            paintLine.points.add(point2D);
        }


    }

}
