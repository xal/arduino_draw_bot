/* File Point2D.java 
 *
 * Project : Java Geometry Library
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

// package

package com.jff.arduino.drawbot.image.convertor.main;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


import static java.lang.Math.*;


/**
 * <p>
 * A point in the plane defined by its 2 Cartesian coordinates x and y. The
 * class provides static methods to compute distance between two points.
 * </p>
 */
public class Point2D  {


	// ===================================================================
	// class variables
	
	/** The x coordinate of this point.*/
	public int x;
	
	/** The y coordinate of this point.*/
	public int y;

    public Point2D() {


    }


    // ===================================================================
	// static methods

	/**
	 * Static factory for creating a new point in cartesian coordinates.
	 * 
	 * @deprecated since 0.11.1
	 */
	@Deprecated
	public static Point2D create(double x, double y) {
		return new Point2D((int)x, (int)y);
	}




	/**
	 * Creates a new point from polar coordinates <code>rho</code> and
	 * <code>theta</code>, from the given point.
	 */
	public static Point2D createPolar(Point2D point, double rho, double theta) {
		return new Point2D((int)(point.x + rho * cos(theta)), (int)(point.y + rho * sin(theta)));
	}



	/**
	 * Computes the Euclidean distance between two points, given by their
	 * coordinates. Uses robust computation (via Math.hypot() method).
	 * 
	 * @return the Euclidean distance between p1 and p2.
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		return hypot(x2 - x1, y2 - y1);
	}

	/**
	 * Computes the Euclidean distance between two points. Uses robust
	 * computation (via Math.hypot() method).
	 * 
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return the Euclidean distance between p1 and p2.
	 */
	public static double distance(Point2D p1, Point2D p2) {
		return hypot(p1.x - p2.x, p1.y - p2.y); 
	}


	@Override
	public String toString() {
		return new String("Point2D("+x+", "+y+")");
	}



	/**
	 * Creates a new Point2D object with same coordinates.
	 */
	@Override
	public Point2D clone() {
		return new Point2D(x, y);
	}

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
