package com.jff.arduino.drawbot.image.convertor.model;

import java.util.ArrayList;
import java.util.List;

public class PaintLine {

    private static final double R_STEP = 10;
    private static final double THETA_STEP = Math.PI / 2 * R_STEP;

    private static final double CYLINDER_RADIUS = 50;
    private static final double CYLINDER_STEPS = 200;

    private static final double MIN_LINE_LENGTH_STEP = Math.PI * 2 * CYLINDER_RADIUS / CYLINDER_STEPS;

    private List<Point2D> points = new ArrayList<Point2D>();

    private static int MAX_GRAY_COLOR_VALUE = 255;
    private static int MIN_GRAY_COLOR_VALUE = 0;


    public List<Point2D> getPoints() {
        return points;
    }


    public static PaintLine createPaintLine(GrayScaleImageBitmap grayScaleImageBitmap) {

        PaintLine paintLine = new PaintLine();

        int width = grayScaleImageBitmap.getWidth();
        int height = grayScaleImageBitmap.getHeight();


        boolean reverse = false;

        double diagonalLength = (float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));

        double rStep = R_STEP;

        for (double i = rStep; i <= diagonalLength; i += rStep) {
            double r = i;

            double thetaStep = CYLINDER_RADIUS / r;

            double theta;
            double nextTheta;

            if (reverse) {
                for (double j = 0; j < Math.PI / 2; j += thetaStep) {

                    theta = j;

                    nextTheta = theta + thetaStep;
                    nextTheta = Math.min(nextTheta, Math.PI);

                    addPointIntoLine(grayScaleImageBitmap, paintLine, r, theta, nextTheta, width, height);

                }

            } else {

                for (double j = Math.PI / 2; j > 0; j -= thetaStep) {

                    theta = j;

                    nextTheta = theta - thetaStep;
                    nextTheta = Math.max(nextTheta, 0);

                    addPointIntoLine(grayScaleImageBitmap, paintLine, r, theta, nextTheta, width, height);


                }


            }
            reverse = !reverse;


        }

        return paintLine;
    }

    private static void addPointIntoLine(GrayScaleImageBitmap grayScaleImageBitmap,
                                         PaintLine paintLine, double r, double theta,
                                         double nextTheta, int width, int height) {


        PolarPoint2D currentPolarPoint2D = new PolarPoint2D(r, theta);

        Point2D currentPoint2D = PolarPoint2D.toCartesian(currentPolarPoint2D);

        PolarPoint2D nextPolarPoint2D = new PolarPoint2D(r, nextTheta);

        Point2D nextPoint2D = PolarPoint2D.toCartesian(nextPolarPoint2D);

        int[][] bitmap = grayScaleImageBitmap.getBitmap();


        int gray = getColorForRegion(bitmap, currentPoint2D, nextPoint2D);


        double thetaStep = Math.abs(nextTheta - theta);
        double rStep = R_STEP;


        double currentTheta = theta;


        double colorRatio = (((float) (MAX_GRAY_COLOR_VALUE - gray - MIN_GRAY_COLOR_VALUE)) /
                MAX_GRAY_COLOR_VALUE - MIN_GRAY_COLOR_VALUE);


        double segmentMinThetaStep = MIN_LINE_LENGTH_STEP / r;


        int segmentStepCount = (int) (thetaStep / segmentMinThetaStep);


        segmentStepCount = (int) (segmentStepCount * colorRatio);

        double segmentThetaStep = thetaStep / segmentStepCount;

        while (true) {


            for (int i = 0; i < rStep; i++) {

                double currentR = r - i;


                PolarPoint2D innerPolarPoint2D = new PolarPoint2D(currentR, currentTheta);

                Point2D innerPoint2D = PolarPoint2D.toCartesian(innerPolarPoint2D);

                if (innerPoint2D.x < width && innerPoint2D.y < height) {

                    paintLine.points.add(innerPoint2D);

                }

            }

            if (theta < nextTheta) {

                currentTheta += segmentThetaStep;
                if (currentTheta > nextTheta) {
                    break;
                }
            } else {
                currentTheta -= segmentThetaStep;
                if (currentTheta < nextTheta) {
                    break;
                }
            }

        }


    }

    private static int getColorForRegion(int[][] bitmap, Point2D currentPoint2D, Point2D nextPoint2D) {

        int color = 0;
        int count = 0;


        int minX = Math.min(currentPoint2D.x, nextPoint2D.x);
        int minY = Math.min(currentPoint2D.y, nextPoint2D.y);
        int maxX = Math.max(currentPoint2D.x, nextPoint2D.x);
        int maxY = Math.max(currentPoint2D.y, nextPoint2D.y);

        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {

                if( i > 0 &&  i < bitmap.length && j > 0 && j < bitmap[i].length) {
                    color += bitmap[i][j];



                    count++;
                }


            }
        }

        if(count != 0) {

        color /= count;
        }


        return color;

    }
}
