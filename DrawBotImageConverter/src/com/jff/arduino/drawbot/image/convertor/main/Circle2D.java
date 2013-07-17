package com.jff.arduino.drawbot.image.convertor.main;

public class Circle2D {


    private Point2D center;

    private double radius;

    public Circle2D(Point2D center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Circle2D(int centerX, int centerY, double radius) {
        this.center = new Point2D(centerX, centerY);
        this.radius = radius;
    }

    public Point2D center() {
        return center;
    }

    public double radius() {
        return  radius;
    }
}
