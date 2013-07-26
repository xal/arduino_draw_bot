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
    private static final int MAX_COUNT_SPACE = 1;

    private double previousLengthRight;
    private double previousLengthLeft;
    private Point2D previousGondolaCenter;
    private final ConverterController controller;
    private Button buttonSave;
    private Label imageView;

    private static int IMAGE_VIEW_WIDTH = 300;
    private static int IMAGE_VIEW_HEIGHT = 500;

//    private static int IMAGE_VIEW_WIDTH = 100;
//    private static int IMAGE_VIEW_HEIGHT = 150;


//
//    static int screenStartX = OFFSET_DRAW;
//    static int screenStartY = OFFSET_DRAW;
//
//    public static int DRAW_WIDTH = IMAGE_VIEW_WIDTH - 2 * OFFSET_DRAW;
//    public static int DRAW_HEIGHT = IMAGE_VIEW_HEIGHT - 2 * OFFSET_DRAW;
//
//    static Point2D startDrawPoint = new Point2D(OFFSET_DRAW, OFFSET_DRAW);
//    static Point2D endDrawPoint = new Point2D(OFFSET_DRAW + DRAW_WIDTH, OFFSET_DRAW + DRAW_HEIGHT);

    static int screenStartX = 85;
    static int screenStartY = 240;

    public static int DRAW_WIDTH = 100;
    public static int DRAW_HEIGHT = 200;

    static Point2D startDrawPoint = new Point2D(screenStartX, screenStartY);
    static Point2D endDrawPoint = new Point2D(185, 440);


    static double drawCanvasDiagonalLength = Point2D.distance(startDrawPoint, endDrawPoint);

    //public static double cylinderRadius = 12.5f;
    public static double cylinderRadius = 13;
    public static double cylinderSteps = 200;

    public static double stepLineLength = 2 * Math.PI * cylinderRadius / cylinderSteps;
    public static double stepRadiusLineLength = stepLineLength * 5;

    public static double stepSectorLineLength = 2 * Math.PI * stepRadiusLineLength ;


//    public static Point2D leftEngineCenter = new Point2D(OFFSET_ENGINE, OFFSET_ENGINE);
//    public static Point2D rightEngineCenter = new Point2D(IMAGE_VIEW_WIDTH - OFFSET_ENGINE, OFFSET_ENGINE);

    public static Point2D leftEngineCenter = new Point2D(30, 0);
    public static Point2D rightEngineCenter = new Point2D(610, 0);


    private Image normalImage;
    private Image scaledImage;
    private Canvas canvas;
    private PaintLine paintLine;
    private Point2D newGondolaCenter;
    private EngineState previousState;
    private double previousRadiusLeft;

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

                        rotate();

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


        newGondolaCenter = null;

        Circle2D circle2DLeft = new Circle2D(leftEngineCenter, previousLengthLeft);
        Circle2D circle2DRight = new Circle2D(rightEngineCenter, previousLengthRight);

        Collection<Point2D> point2Ds = CircleUtils.circlesIntersections(circle2DLeft, circle2DRight);


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

        //previousRadiusLeft = Point2D.distance(startDrawPoint, newGondolaCenter);
        previousRadiusLeft = Point2D.distance(leftEngineCenter, newGondolaCenter);

        if (newGondolaCenter == null) {
            System.out.println("error");
            throw new IllegalArgumentException();
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

        try {
            doWork(paintLine);

        } catch (Exception e) {
            e.printStackTrace();
        }

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

        previousGondolaCenter = startDrawPoint;

        previousRadiusLeft = Point2D.distance(leftEngineCenter, previousGondolaCenter);




        while (distanceBetweenStartAndGondola < drawCanvasDiagonalLength) {

            //System.out.println(distanceBetweenStartAndGondola + " " + drawCanvasDiagonalLength);


            boolean rowEnd = false;

            boolean segmentUp = false;


            double startRowRadius = previousRadiusLeft;



            while (!rowEnd) {





                if (up) {

                    //System.out.println("leftdown");

                    Point2D startSegmentPoint = previousGondolaCenter;

                    double startTheta = Math.atan2(startSegmentPoint.y, startSegmentPoint.x);

                    Point2D currentSegmentPoint = startSegmentPoint;

                    boolean segmentEnd = false;

                    while(!segmentEnd) {


                        double currentCircleLength = Math.PI * 2 * previousRadiusLeft;



                        double currentTheta = Math.atan2(currentSegmentPoint.y,currentSegmentPoint.x);

                        double deltaTheta = Math.abs(startTheta - currentTheta);

                        double pieceTheta = deltaTheta/(Math.PI * 2);


                        double currentCirclePieceLength = currentCircleLength * pieceTheta;
                        currentCirclePieceLength = stepSectorLineLength;

                        pieceTheta = currentCirclePieceLength/currentCircleLength;

                        deltaTheta = pieceTheta * Math.PI * 2;

                        double nextTheta = currentTheta - deltaTheta;

                        PolarPoint2D polarPoint = new PolarPoint2D(previousRadiusLeft, nextTheta);
                        Point2D nextPoint = PolarPoint2D.toCartesian(polarPoint);

                        int colorForRegion = getColorForRegion(grayScaleImageBitmap, currentSegmentPoint, nextPoint);


                        int maxColor = 257;

                        double colorRatio = ((double)(maxColor - colorForRegion))/maxColor;
                        System.out.println(colorForRegion +" "+colorRatio);
                        //int countSpaces = (int) (MAX_COUNT_SPACE * colorRatio);
                        int countSpaces = MAX_COUNT_SPACE;

                        if (segmentUp) {


                            while (previousRadiusLeft  - startRowRadius < stepRadiusLineLength) {
                                previousState = EngineState.LEFT_CLOCKWISE;
                                doCalculate();
                                paintLine.pathEngine.add(previousState);



                            }



                        } else {

                            //  for(int i = 0; i < 10; i++) {
//                            while (previousRadiusLeft > startRowRadius)      {
                            while (previousRadiusLeft > startRowRadius)      {
                                previousState = EngineState.LEFT_ANTICLOCKWISE;
                                doCalculate();
                                paintLine.pathEngine.add(previousState);
                            }
                        }





                        for(int i = 0; i < countSpaces; i++) {

                            previousState = EngineState.RIGHT_CLOCKWISE;
                            doCalculate();
                            paintLine.pathEngine.add(previousState);

                            if (previousGondolaCenter.y < startDrawPoint.y) {
                                break;
                            }

                            if (previousGondolaCenter.x > endDrawPoint.x) {
                                break;
                            }

                        }


                        currentSegmentPoint = previousGondolaCenter;

                        segmentUp = !segmentUp;



                       currentTheta = Math.atan2(currentSegmentPoint.y,currentSegmentPoint.x);

                        deltaTheta = Math.abs(startTheta - currentTheta);

                        pieceTheta = deltaTheta/(Math.PI * 2);


                        currentCirclePieceLength = currentCircleLength * pieceTheta;

//                        System.out.println(Math.toDegrees(startTheta) + " -> " + Math.toDegrees(currentTheta));
//                        System.out.println(currentCirclePieceLength +  " " + stepSectorLineLength);
                        if(currentCirclePieceLength > stepSectorLineLength && !segmentUp) {
                            segmentEnd = true;
                        }

                        if (previousGondolaCenter.y < startDrawPoint.y && !segmentUp) {
                            segmentEnd = true;
                        }

                        if (previousGondolaCenter.x > endDrawPoint.x && !segmentUp) {
                            segmentEnd = true;
                        }

                    }









                    if (previousGondolaCenter.y < startDrawPoint.y) {
                        rowEnd = true;
                    }

                    if (previousGondolaCenter.x > endDrawPoint.x) {
                        rowEnd = true;
                    }

                } else {


                    Point2D startSegmentPoint = previousGondolaCenter;

                    double startTheta = Math.atan2(startSegmentPoint.y, startSegmentPoint.x);

                    Point2D currentSegmentPoint = startSegmentPoint;

                    boolean segmentEnd = false;

                    while(!segmentEnd) {




                        if (segmentUp) {


                            while (previousRadiusLeft  - startRowRadius < stepRadiusLineLength) {
                                previousState = EngineState.LEFT_CLOCKWISE;
                                doCalculate();
                                paintLine.pathEngine.add(previousState);



                            }



                        } else {

                            //  for(int i = 0; i < 10; i++) {
                            while (previousRadiusLeft > startRowRadius)      {
                                previousState = EngineState.LEFT_ANTICLOCKWISE;
                                doCalculate();
                                paintLine.pathEngine.add(previousState);
                            }
                        }

                        double currentCircleLength = Math.PI * 2 * previousRadiusLeft;



                        double currentTheta = Math.atan2(currentSegmentPoint.y,currentSegmentPoint.x);

                        double deltaTheta = Math.abs(startTheta - currentTheta);

                        double pieceTheta = deltaTheta/(Math.PI * 2);


                        double currentCirclePieceLength = currentCircleLength * pieceTheta;
                        currentCirclePieceLength = stepSectorLineLength;

                        pieceTheta = currentCirclePieceLength/currentCircleLength;

                        deltaTheta = pieceTheta * Math.PI * 2;

                        double nextTheta = currentTheta + deltaTheta;

                        PolarPoint2D polarPoint = new PolarPoint2D(previousRadiusLeft, nextTheta);
                        Point2D nextPoint = PolarPoint2D.toCartesian(polarPoint);

                        int colorForRegion = getColorForRegion(grayScaleImageBitmap, currentSegmentPoint, nextPoint);


                        int maxColor = 256;

                        double colorRatio = (maxColor - colorForRegion)/maxColor;

//                        int countSpaces = (int) (MAX_COUNT_SPACE * colorRatio);
                         int countSpaces = MAX_COUNT_SPACE;


                        for(int i = 0; i < countSpaces; i++) {

                            previousState = EngineState.RIGHT_ANTICLOCKWISE;
                            doCalculate();
                            paintLine.pathEngine.add(previousState);

                            if (previousGondolaCenter.x < startDrawPoint.x) {
                                break;
                            }

                            if (previousGondolaCenter.y > endDrawPoint.y) {
                             break;
                            }
                        }


                        currentSegmentPoint = previousGondolaCenter;

                        segmentUp = !segmentUp;



                        currentTheta = Math.atan2(currentSegmentPoint.y,currentSegmentPoint.x);

                        deltaTheta = Math.abs(startTheta - currentTheta);

                        pieceTheta = deltaTheta/(Math.PI * 2);


                        currentCirclePieceLength = currentCircleLength * pieceTheta;
//                        System.out.println(Math.toDegrees(startTheta) + " -> " + Math.toDegrees(currentTheta));
//                        System.out.println(currentCirclePieceLength +  " " + stepSectorLineLength);
                        if(currentCirclePieceLength > stepSectorLineLength && !segmentUp) {
                            segmentEnd = true;
                        }

                        if (previousGondolaCenter.x < startDrawPoint.x && !segmentUp) {
                            segmentEnd = true;
                        }

                        if (previousGondolaCenter.y > endDrawPoint.y && !segmentUp) {
                            segmentEnd = true;
                        }

                    }






                    if (previousGondolaCenter.x < startDrawPoint.x) {
                        rowEnd = true;
                    }

                    if (previousGondolaCenter.y > endDrawPoint.y) {
                        rowEnd = true;
                    }

                }


                Point2D startRowPoint = previousGondolaCenter;


//                System.out.println("up = " + up + " " + previousGondolaCenter);

            }

            double startRadius = previousRadiusLeft;
            while (previousRadiusLeft - startRadius < rStep) {

                if (up) {

                    if (previousGondolaCenter.x < endDrawPoint.x) {
                        right(paintLine);
                    } else {
                        down(paintLine);
                    }

                } else {

                    if (previousGondolaCenter.y < endDrawPoint.y) {

                        down(paintLine);
                    } else {
                        right(paintLine);
                    }


                }


            }

            up = !up;


            double prev = distanceBetweenStartAndGondola;

            distanceBetweenStartAndGondola = Point2D.distance(startDrawPoint, previousGondolaCenter);


            System.out.println(" dist " + distanceBetweenStartAndGondola);

        }

    }

    private void doCalculate() {
        rotate();
        calculateNewGondolaCenter();
        previousGondolaCenter = newGondolaCenter;
    }

    private void right(PaintLine paintLine) {


        int steps = 0;


        Point2D startDownPoint = previousGondolaCenter;

        Point2D currentDownPoint = startDownPoint;


        double startRadius = previousRadiusLeft;


        while (previousRadiusLeft - startRadius < stepRadiusLineLength) {

            while (currentDownPoint.y >= startDownPoint.y) {
                previousState = EngineState.RIGHT_CLOCKWISE;
                doCalculate();
                paintLine.pathEngine.add(previousState);
                currentDownPoint = previousGondolaCenter;

                steps++;
            }


            while (currentDownPoint.y < startDownPoint.y) {
                previousState = EngineState.LEFT_CLOCKWISE;
                doCalculate();
                paintLine.pathEngine.add(previousState);
                currentDownPoint = previousGondolaCenter;

                steps++;
            }

            currentDownPoint = previousGondolaCenter;

        }

        //   System.out.println("right " + steps);


    }

    private void down(PaintLine paintLine) {

        int steps = 0;


        Point2D startDownPoint = previousGondolaCenter;

        Point2D currentDownPoint = startDownPoint;


        double startRadius = previousRadiusLeft;


        while (previousRadiusLeft - startRadius < stepRadiusLineLength) {

            while (currentDownPoint.x >= startDownPoint.x) {
                previousState = EngineState.RIGHT_ANTICLOCKWISE;
                doCalculate();
                paintLine.pathEngine.add(previousState);
                currentDownPoint = previousGondolaCenter;
                steps++;
            }


            while (currentDownPoint.x < startDownPoint.x) {
                previousState = EngineState.LEFT_CLOCKWISE;
                doCalculate();
                paintLine.pathEngine.add(previousState);
                currentDownPoint = previousGondolaCenter;
                steps++;
            }

            currentDownPoint = previousGondolaCenter;

            //System.out.println(previousRadiusLeft - startRadius);
        }

        //  System.out.println("down " + steps);

    }

    private void downRight(PaintLine paintLine) {

        //   System.out.println("downRight");

        Point2D startPoint = previousGondolaCenter;

        Point2D currentPoint = startPoint;


        double startRadius = previousRadiusLeft;


        while (previousRadiusLeft - startRadius < stepRadiusLineLength) {

            previousState = EngineState.LEFT_CLOCKWISE;
            doCalculate();
            paintLine.pathEngine.add(previousState);
            currentPoint = previousGondolaCenter;


        }


//        previousState = EngineState.RIGHT_CLOCKWISE;
//        doCalculate();
//        paintLine.pathEngine.add(previousState);
//        currentPoint = previousGondolaCenter;


    }

    private void upLeft(PaintLine paintLine) {

        //  System.out.println("downRight");

        Point2D startPoint = previousGondolaCenter;

        Point2D currentPoint = startPoint;


        double startRadius = previousRadiusLeft;


        while (startRadius - previousRadiusLeft < stepRadiusLineLength) {

            previousState = EngineState.LEFT_ANTICLOCKWISE;
            doCalculate();
            paintLine.pathEngine.add(previousState);
            currentPoint = previousGondolaCenter;


            currentPoint = previousGondolaCenter;

        }


//        previousState = EngineState.RIGHT_CLOCKWISE;
//        doCalculate();
//        paintLine.pathEngine.add(previousState);
//        currentPoint = previousGondolaCenter;


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

        currentPoint2D = new Point2D(currentPoint2D.x - startDrawPoint.x, currentPoint2D.y - startDrawPoint.y);
        nextPoint2D = new Point2D(nextPoint2D .x - startDrawPoint.x, nextPoint2D.y - startDrawPoint.y);

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
