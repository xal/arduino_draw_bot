package com.jff.arduino.drawbot.image.convertor.ui;

import com.jff.arduino.drawbot.image.convertor.controller.ConverterController;
import com.jff.arduino.drawbot.image.convertor.main.Circle2D;
import com.jff.arduino.drawbot.image.convertor.main.CircleUtils;
import com.jff.arduino.drawbot.image.convertor.main.Point2D;
import com.jff.arduino.drawbot.image.convertor.model.EngineState;
import com.jff.arduino.drawbot.image.convertor.model.GrayScaleImageBitmap;
import com.jff.arduino.drawbot.image.convertor.model.PaintLine;
import com.jff.arduino.drawbot.image.convertor.model.PolarPoint2D;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class ImageOutputPanel extends Composite {
    private static final int OFFSET_ENGINE = 10;

    private static final int OFFSET_DRAW = 20;
    private double previousLengthRight;
    private double previousLengthLeft;
    private Point2D previousGondolaCenter;
    private final ConverterController controller;
    private Button buttonSave;
    private Label imageView;

    private static int IMAGE_VIEW_WIDTH = 300;
    private static int IMAGE_VIEW_HEIGHT = 500;


    static int screenStartX = OFFSET_DRAW;
    static int screenStartY = OFFSET_DRAW;

    public static int DRAW_WIDTH = IMAGE_VIEW_WIDTH - 2 * OFFSET_DRAW;
    public static int DRAW_HEIGHT = IMAGE_VIEW_HEIGHT - 2 * OFFSET_DRAW;


    static Point2D startDrawPoint = new Point2D(OFFSET_DRAW, OFFSET_DRAW);
    static Point2D endDrawPoint = new Point2D(OFFSET_DRAW + DRAW_WIDTH, OFFSET_DRAW + DRAW_HEIGHT);

    static double drawCanvasDiagonalLength = Point2D.distance(startDrawPoint, endDrawPoint);

    public static double cylinderRadius = 12.5f;
    public static double cylinderSteps = 200;

    public static double stepLineLength = 2 * Math.PI * cylinderRadius / cylinderSteps;
    public static double stepSectorLineLength = stepLineLength;
    public static double stepRadiusLineLength = stepLineLength * 10;


    public static Point2D leftEngineCenter = new Point2D(OFFSET_ENGINE, OFFSET_ENGINE);
    public static Point2D rightEngineCenter = new Point2D(IMAGE_VIEW_WIDTH - OFFSET_ENGINE, OFFSET_ENGINE);

    private Image normalImage;
    private Image scaledImage;
    private Canvas canvas;
    private PaintLine paintLine;
    private Point2D newGondolaCenter;
    private EngineState previousState;

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

                gc.drawRectangle(screenStartX, screenStartY, DRAW_WIDTH, DRAW_HEIGHT);

                gc.drawOval((int) leftEngineCenter.x - 5, (int) leftEngineCenter.y - 5, 10, 10);
                gc.drawOval((int) rightEngineCenter.x - 5, (int) rightEngineCenter.y - 5, 10, 10);


                if (paintLine != null) {


                    previousGondolaCenter = new Point2D(screenStartX, screenStartY);

                    previousLengthLeft = Point2D.distance(leftEngineCenter, previousGondolaCenter);
                    previousLengthRight = Point2D.distance(rightEngineCenter, previousGondolaCenter);

                    for (EngineState state : paintLine.pathEngine.states) {

                        previousState = state;

//                        System.out.println(previousLengthLeft + " " + previousLengthRight);
                        rotate();
//                        System.out.println(previousLengthLeft + " " + previousLengthRight);

                        calculateNewGondolaCenter();

                        gc.drawLine(previousGondolaCenter.x, previousGondolaCenter.y, newGondolaCenter.x, newGondolaCenter.y);

//                        System.out.println(previousGondolaCenter + " " +newGondolaCenter);

                        previousGondolaCenter = newGondolaCenter;


                    }

                }


            }
        });


        buttonSave = new Button(this, SWT.NONE);
        buttonSave.setText("Save");


        buttonSave.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {

                paintLine.printPath();


            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

    }

    private void calculateNewGondolaCenter() {

        EngineState state = previousState;

        System.out.println(state);

        newGondolaCenter = null;

        Circle2D circle2DLeft = new Circle2D(leftEngineCenter, previousLengthLeft);
        Circle2D circle2DRight = new Circle2D(rightEngineCenter, previousLengthRight);

        Collection<Point2D> point2Ds = CircleUtils.circlesIntersections(circle2DLeft, circle2DRight);
//        System.out.println(point2Ds);
        for (Point2D point2D : point2Ds) {
            switch (state) {

                case LEFT_CLOCKWISE:
                    if (point2D.x >= previousGondolaCenter.x && point2D.y >= previousGondolaCenter.y) {


                        if (newGondolaCenter == null) {

                            newGondolaCenter = point2D;
                        } else {
                            double distance1 = Point2D.distance(previousGondolaCenter, newGondolaCenter);
                            double distance2 = Point2D.distance(previousGondolaCenter, point2D);

                            if (distance2 < distance1) {
                                newGondolaCenter = point2D;
                            }
                        }
                    }
                    break;
                case LEFT_ANTICLOCKWISE:
                    if (point2D.x <= previousGondolaCenter.x && point2D.y <= previousGondolaCenter.y) {


                        if (newGondolaCenter == null) {

                            newGondolaCenter = point2D;
                        } else {
                            double distance1 = Point2D.distance(previousGondolaCenter, newGondolaCenter);
                            double distance2 = Point2D.distance(previousGondolaCenter, point2D);

                            if (distance2 < distance1) {
                                newGondolaCenter = point2D;
                            }
                        }
                    }

                    break;
                case RIGHT_CLOCKWISE:
                    if (point2D.x >= previousGondolaCenter.x && point2D.y <= previousGondolaCenter.y) {
                        if (newGondolaCenter == null) {

                            newGondolaCenter = point2D;
                        } else {
                            double distance1 = Point2D.distance(previousGondolaCenter, newGondolaCenter);
                            double distance2 = Point2D.distance(previousGondolaCenter, point2D);

                            if (distance2 < distance1) {
                                newGondolaCenter = point2D;
                            }
                        }


                    }

                    break;
                case RIGHT_ANTICLOCKWISE:
                    if (point2D.x <= previousGondolaCenter.x && point2D.y >= previousGondolaCenter.y) {


                        if (newGondolaCenter == null) {

                            newGondolaCenter = point2D;
                        } else {
                            double distance1 = Point2D.distance(previousGondolaCenter, newGondolaCenter);
                            double distance2 = Point2D.distance(previousGondolaCenter, point2D);

                            if (distance2 < distance1) {
                                newGondolaCenter = point2D;
                            }
                        }
                    }

                    break;
            }
        }

        if (newGondolaCenter == null) {
            System.out.println("error");
        }
    }

    private void rotate() {
        EngineState state = previousState;
        switch (state) {

            case LEFT_CLOCKWISE:
                previousLengthLeft += stepLineLength;
                break;
            case LEFT_ANTICLOCKWISE:
                previousLengthLeft -= stepLineLength;
                break;
            case RIGHT_CLOCKWISE:
                previousLengthRight -= stepLineLength;
                break;
            case RIGHT_ANTICLOCKWISE:
                previousLengthRight += stepLineLength;
                break;
        }
    }


    public void changeOutputImageData(Image imageBitmapFromGrayScale) {

        if (normalImage != null) {
            normalImage.dispose();
        }

        normalImage = imageBitmapFromGrayScale;

        showScaledImage();

    }


    private void showScaledImage() {

        Device display = getDisplay();
        if (scaledImage != null) {
            scaledImage.dispose();
        }

        scaledImage = new Image(display, normalImage.getImageData().scaledTo(IMAGE_VIEW_WIDTH, IMAGE_VIEW_HEIGHT));
        imageView.setImage(scaledImage);


        this.layout();

    }

    public void changePaintLine(PaintLine paintLine) {

        this.paintLine = paintLine;

//        PaintLine paintLine2 = new PaintLine();
//        for (int i = 0; i < 100; i++) {
//            paintLine2.pathEngine.add(EngineState.LEFT_CLOCKWISE);
//        }
//
//        for (int i = 0; i < 100; i++) {
//            paintLine2.pathEngine.add(EngineState.RIGHT_CLOCKWISE);
//        }
//        for (int i = 0; i < 100; i++) {
//            paintLine2.pathEngine.add(EngineState.LEFT_CLOCKWISE);
//        }
//        for (int i = 0; i < 100; i++) {
//            paintLine2.pathEngine.add(EngineState.RIGHT_ANTICLOCKWISE);
//        }
//        for (int i = 0; i < 100; i++) {
//            paintLine2.pathEngine.add(EngineState.LEFT_ANTICLOCKWISE);
//        }
//
//        this.paintLine = paintLine2;

        doWork(paintLine);

        paintLine.printPath();


        canvas.redraw();
    }


    private void doWork(PaintLine paintLine) {

        GrayScaleImageBitmap grayScaleImageBitmap = paintLine.bitmap;

        int width = grayScaleImageBitmap.getWidth();
        int height = grayScaleImageBitmap.getHeight();


        boolean up = true;


        double diagonalLength = (float) Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));


        previousGondolaCenter = startDrawPoint;

        previousLengthLeft = Point2D.distance(leftEngineCenter, previousGondolaCenter);
        previousLengthRight = Point2D.distance(rightEngineCenter, previousGondolaCenter);

        double rStep = stepRadiusLineLength;


        double distanceBetweenStartAndGondola;

        distanceBetweenStartAndGondola = Point2D.distance(startDrawPoint, previousGondolaCenter);

        while (distanceBetweenStartAndGondola < drawCanvasDiagonalLength) {

//            System.out.println("next");


            Point2D startSectorPoint = previousGondolaCenter;

            double distanceBetweenLines = 0;
            while (distanceBetweenLines < rStep) {

//                System.out.println(distanceBetweenLines);

                if (up) {

                    if (previousGondolaCenter.y < endDrawPoint.y) {

                        down(paintLine);
                    } else {
                        right(paintLine);
                    }

                } else {

                    if (previousGondolaCenter.x < endDrawPoint.x) {
                        right(paintLine);
                    } else {
                        down(paintLine);
                    }


                }


                distanceBetweenLines = Point2D.distance(startSectorPoint, previousGondolaCenter);
            }

            boolean stop = false;

            while (!stop) {

//                if (previousGondolaCenter.x < startDrawPoint.x ||
//                        previousGondolaCenter.y < startDrawPoint.y ||
//                        previousGondolaCenter.x > endDrawPoint.x ||
//                        previousGondolaCenter.y > endDrawPoint.y
//                        ) {
//                    stop = true;
//                    continue;
//                }

                if (up) {


                    previousState = EngineState.RIGHT_CLOCKWISE;
                    rotate();
                    calculateNewGondolaCenter();
                    previousGondolaCenter = newGondolaCenter;
                    paintLine.pathEngine.add(previousState);

                    if (previousGondolaCenter.y < startDrawPoint.y) {
                        stop = true;
                    }

                    if (previousGondolaCenter.x > endDrawPoint.x) {
                        stop = true;
                    }

                } else {


                    previousState = EngineState.RIGHT_ANTICLOCKWISE;
                    rotate();
                    calculateNewGondolaCenter();
                    previousGondolaCenter = newGondolaCenter;
                    paintLine.pathEngine.add(previousState);

                    if (previousGondolaCenter.x < startDrawPoint.x) {
                        stop = true;
                    }

                    if (previousGondolaCenter.y > endDrawPoint.y) {
                        stop = true;
                    }

                }

            }

            up = !up;


            distanceBetweenStartAndGondola = Point2D.distance(startDrawPoint, previousGondolaCenter);

        }
//
//        double rStep = R_STEP;
//
//        for (double i = rStep; i <= diagonalLength; i += rStep) {
//            double r = i;
//
//            double thetaStep = CYLINDER_RADIUS / r;
//
//            double theta;
//            double nextTheta;
//
//            if (up) {
//                for (double j = 0; j < Math.PI / 2; j += thetaStep) {
//
//                    theta = j;
//
//                    nextTheta = theta + thetaStep;
//                    nextTheta = Math.min(nextTheta, Math.PI);
//
//                    addPointIntoLine(grayScaleImageBitmap, paintLine, r, theta, nextTheta, width, height);
//
//                }
//
//
//                int steps = howManyStepsBetweenLines(r);
//
//                for (int k = 0; k < steps; k++) {
//
//                    state = EngineState.RIGHT_CLOCKWISE;
//                    paintLine.pathEngine.add(state);
//
//
//                    state = EngineState.LEFT_CLOCKWISE;
//                    paintLine.pathEngine.add(state);
//
//                }
//
//
//            } else {
//
//                for (double j = Math.PI / 2; j > 0; j -= thetaStep) {
//
//                    theta = j;
//
//                    nextTheta = theta - thetaStep;
//                    nextTheta = Math.max(nextTheta, 0);
//
//                    addPointIntoLine(grayScaleImageBitmap, paintLine, r, theta, nextTheta, width, height);
//
//
//                }
//
//
//                int steps = howManyStepsBetweenLines(r);
//
//                for (int k = 0; k < steps; k++) {
//
//
//                }
//
//
//            }
//            up = !up;
//
//
//        }
    }

    private void right(PaintLine paintLine) {
        previousState = EngineState.LEFT_CLOCKWISE;
        rotate();
        calculateNewGondolaCenter();


        previousGondolaCenter = newGondolaCenter;
        paintLine.pathEngine.add(previousState);

        previousState = EngineState.RIGHT_CLOCKWISE;
        rotate();
        calculateNewGondolaCenter();
        previousGondolaCenter = newGondolaCenter;
        paintLine.pathEngine.add(previousState);

    }

    private void down(PaintLine paintLine) {
        previousState = EngineState.LEFT_CLOCKWISE;
        rotate();
        calculateNewGondolaCenter();
        previousGondolaCenter = newGondolaCenter;
        paintLine.pathEngine.add(previousState);

        previousState = EngineState.RIGHT_ANTICLOCKWISE;
        rotate();
        calculateNewGondolaCenter();
        previousGondolaCenter = newGondolaCenter;
        paintLine.pathEngine.add(previousState);
    }

//
//
//    private static void addPointIntoLine(GrayScaleImageBitmap grayScaleImageBitmap,
//                                         PaintLine paintLine, double r, double theta,
//                                         double nextTheta, int width, int height) {
//
//
//        PolarPoint2D currentPolarPoint2D = new PolarPoint2D(r, theta);
//
//        Point2D currentPoint2D = PolarPoint2D.toCartesian(currentPolarPoint2D);
//
//        PolarPoint2D nextPolarPoint2D = new PolarPoint2D(r, nextTheta);
//
//        Point2D nextPoint2D = PolarPoint2D.toCartesian(nextPolarPoint2D);
//
//        int[][] bitmap = grayScaleImageBitmap.getBitmap();
//
//
//        int gray = getColorForRegion(bitmap, currentPoint2D, nextPoint2D);
//
//
//        double thetaStep = Math.abs(nextTheta - theta);
//        double rStep = R_STEP;
//
//
//        double currentTheta = theta;
//
//
//        double colorRatio = (((float) (MAX_GRAY_COLOR_VALUE - gray - MIN_GRAY_COLOR_VALUE)) /
//                MAX_GRAY_COLOR_VALUE - MIN_GRAY_COLOR_VALUE);
//
//
//        double segmentMinThetaStep = MIN_LINE_LENGTH_STEP / r;
//
//
//        int segmentStepCount = (int) (thetaStep / segmentMinThetaStep);
//
//
//        segmentStepCount = (int) (segmentStepCount * colorRatio);
//
//        rStep *= colorRatio;
//
//        double segmentThetaStep = thetaStep / segmentStepCount;
//
//        boolean reverse = false;
//
//        while (true) {
//
//            Point2D innerPoint2D = null;
//
//            if (reverse) {
//
//                for (int i = (int) (rStep - 1); i >= 0; i--) {
//
//                    double currentR = r - i;
//
//
//                    PolarPoint2D innerPolarPoint2D = new PolarPoint2D(currentR, currentTheta);
//
//                    innerPoint2D = PolarPoint2D.toCartesian(innerPolarPoint2D);
//
//                    if (innerPoint2D.x < width && innerPoint2D.y < height) {
//
//
//                        paintLine.points.add(innerPoint2D);
//
//                    }
//
//                    EngineState state;
//
////                        if(theta > nextTheta) {
//
//                    state = EngineState.LEFT_ANTICLOCKWISE;
////                        }   else {
////                            state = EngineState.RIGHT_ANTICLOCKWISE;
////                        }
//
//
////                        paintLine.pathEngine.add(state);
//
//                }
//
//            } else {
//                for (int i = 0; i < rStep; i++) {
//
//                    double currentR = r - i;
//
//
//                    PolarPoint2D innerPolarPoint2D = new PolarPoint2D(currentR, currentTheta);
//
//                    innerPoint2D = PolarPoint2D.toCartesian(innerPolarPoint2D);
//
//                    if (innerPoint2D.x < width && innerPoint2D.y < height) {
//
//
//                        paintLine.points.add(innerPoint2D);
//
//                    }
//
//                    EngineState state;
//
////                        if(theta > nextTheta) {
//
//                    state = EngineState.LEFT_CLOCKWISE;
////                        }   else {
////                            state = EngineState.RIGHT_CLOCKWISE;
////                        }
//
//
////                        paintLine.pathEngine.add(state);
//
//                }
//            }
//
//            reverse = !reverse;
//
//
//            if (theta < nextTheta) {
//
//                currentTheta += segmentThetaStep;
//
//
//                EngineState state;
//
//                if (innerPoint2D != null && innerPoint2D.x < width && innerPoint2D.y < height) {
//
//                    state = EngineState.RIGHT_CLOCKWISE;
//                    paintLine.pathEngine.add(state);
//
//                }
//
//
//                if (currentTheta > nextTheta && !reverse) {
//                    break;
//                }
//            } else {
//                currentTheta -= segmentThetaStep;
//
//
//                EngineState state;
//
//
//                if (innerPoint2D != null && innerPoint2D.x < width && innerPoint2D.y < height) {
//
//
//                    state = EngineState.RIGHT_ANTICLOCKWISE;
//                    paintLine.pathEngine.add(state);
//
//                }
//
//
////                state = EngineState.LEFT_ANTICLOCKWISE;
////                paintLine.pathEngine.add(state);
//
//
//                if (currentTheta < nextTheta && !reverse) {
//                    break;
//                }
//            }
//
//
//        }
//
//
//    }

    private static int getColorForRegion(GrayScaleImageBitmap grayScaleImageBitmap, Point2D currentPoint2D, Point2D nextPoint2D) {

        int[][] bitmap = grayScaleImageBitmap.getBitmap();

        int color = 0;
        int count = 0;


        int minX = Math.min(currentPoint2D.x, nextPoint2D.x);
        int minY = Math.min(currentPoint2D.y, nextPoint2D.y);
        int maxX = Math.max(currentPoint2D.x, nextPoint2D.x);
        int maxY = Math.max(currentPoint2D.y, nextPoint2D.y);

        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {

                if (i > 0 && i < bitmap.length && j > 0 && j < bitmap[i].length) {
                    color += bitmap[i][j];


                    count++;
                }


            }
        }

        if (count != 0) {

            color /= count;
        }


        return color;

    }


}
