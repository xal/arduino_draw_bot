package com.jff.arduino.drawbot.image.convertor.model;

import com.jff.arduino.drawbot.image.convertor.main.Point2D;

public class PolarPoint2D {

    public double r;
    public double theta;

    public PolarPoint2D() {

    }


    public static PolarPoint2D fromCartesian(Point2D point2D) {
        PolarPoint2D polarPoint2D = new PolarPoint2D();

        polarPoint2D.r = Math.sqrt(Math.pow(point2D.x, 2) + Math.pow(point2D.y, 2));
        if (polarPoint2D.r == 0) {

            polarPoint2D.theta = 0;
        } else {


            polarPoint2D.theta = Math.atan2(point2D.y, point2D.x);
        }

        return polarPoint2D;
    }

    public static Point2D toCartesian(PolarPoint2D polarPoint2D) {
        Point2D point2D = new Point2D();


        point2D.x = (int) (polarPoint2D.r * Math.cos(polarPoint2D.theta));
        point2D.y = (int) (polarPoint2D.r * Math.sin(polarPoint2D.theta));


        return point2D;
    }


    public PolarPoint2D(double r, double theta) {
        this.r = r;
        this.theta = theta;
    }
}