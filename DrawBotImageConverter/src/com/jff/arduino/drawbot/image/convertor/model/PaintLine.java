package com.jff.arduino.drawbot.image.convertor.model;

import java.util.ArrayList;
import java.util.List;

public class PaintLine {

    private static final double R_STEP = 20;
    private static final double THETA_STEP = Math.PI / 2 * R_STEP;

    private static final double CYLINDER_RADIUS = 50;
    private static final double CYLINDER_STEPS = 200;

    private static final double MIN_LINE_LENGTH_STEP = Math.PI * 2 * CYLINDER_RADIUS / CYLINDER_STEPS;

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

        double rStep = R_STEP;

        for (double i = 0; i <= diagonalLength; i += rStep) {
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



//        PolarPoint2D currentPolarPoint2D = new PolarPoint2D(r, theta);
//
//        Point2D currentPoint2D = PolarPoint2D.toCartesian(currentPolarPoint2D);

        int[][] bitmap = grayScaleImageBitmap.getBitmap();




//            int gray = bitmap[currentPoint2D.x][currentPoint2D.y];


            double thetaStep = nextTheta - theta;
            double rStep = R_STEP;



            double currentTheta = theta;


            while(true) {

                double minStep =  MIN_LINE_LENGTH_STEP/ r;

                for(int i = 0; i < rStep; i++) {

                    double currentR = r - i;




                    PolarPoint2D innerPolarPoint2D = new PolarPoint2D(currentR, currentTheta);

                    Point2D innerPoint2D = PolarPoint2D.toCartesian(innerPolarPoint2D);

                    if (innerPoint2D.x < width && innerPoint2D.y < height) {

                        paintLine.points.add(innerPoint2D);

                    }

                }

                if(theta < nextTheta) {

                    currentTheta += minStep;
                    if(currentTheta > nextTheta) {
                        break;
                    }
                } else {
                    currentTheta -= minStep;
                    if(currentTheta < nextTheta) {
                        break;
                    }
                }

            }








    }

}
