package com.jff.arduino.drawbot.image.convertor.main;



import java.util.*;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class CircleUtils {

    /**
     * Computes the intersections points between two circles or circular shapes.
     *
     * @param circle1
     *            an instance of circle or circle arc
     * @param circle2
     *            an instance of circle or circle arc
     * @return a collection of 0, 1 or 2 intersection points
     */
    public static Collection<Point2D> circlesIntersections(Circle2D circle1,
                                                           Circle2D circle2) {
        // extract center and radius of each circle
        Point2D center1 = circle1.center();
        Point2D center2 = circle2.center();
        double r1 = circle1.radius();
        double r2 = circle2.radius();

        double d = Point2D.distance(center1, center2);

        // case of no intersection
        if (d < abs(r1 - r2) || d > (r1 + r2))
            return new ArrayList<Point2D>(0);

        // Angle of line from center1 to center2
        double angle = horizontalAngle(center1, center2);

        // position of intermediate point
        double d1 = d / 2 + (r1 * r1 - r2 * r2) / (2 * d);
        Point2D tmp = Point2D.createPolar(center1, d1, angle);

        // distance between intermediate point and each intersection
        double h = sqrt(r1 * r1 - d1 * d1);

        // create empty array
        ArrayList<Point2D> intersections = new ArrayList<Point2D>(2);

        // Add the 2 intersection points
        Point2D p1 = Point2D.createPolar(tmp, h, angle + PI / 2);
        intersections.add(p1);
        Point2D p2 = Point2D.createPolar(tmp, h, angle - PI / 2);
        intersections.add(p2);

        return intersections;
    }

    /**
     * Returns the horizontal angle formed by the line joining the two given
     * points.
     */
    public static double horizontalAngle(Point2D p1,	Point2D p2) {

        double M_2PI 	= Math.PI * 2;
        return (Math.atan2(p2.y - p1.y, p2.x - p1.x) + M_2PI) % (M_2PI);
    }
}