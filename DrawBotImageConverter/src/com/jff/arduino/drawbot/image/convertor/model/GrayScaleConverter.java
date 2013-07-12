package com.jff.arduino.drawbot.image.convertor.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import java.awt.*;

public class GrayScaleConverter {


    public static GrayScaleImageBitmap createGrayScaleBitmap(Image image) {

        ImageData imageData = image.getImageData();

        int width = imageData.width;
        int height = imageData.height;

        int[][] grayScaleBitmap = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = imageData.getPixel(i, j);

                int blue = pixel & 255;
                int green = (pixel >> 8) & 255;
                int red = (pixel >> 16) & 255;

                int max = Math.max(blue, green);
                max = Math.max(max, red);

                int min = Math.min(blue, green);
                min = Math.min(min, red);

                int gray = (max + min) / 2;


                grayScaleBitmap[i][j] = gray;
            }
        }

        GrayScaleImageBitmap grayScaleImageBitmap = new GrayScaleImageBitmap(grayScaleBitmap);

        return grayScaleImageBitmap;

    }

    public static ImageData createImageBitmapFromGrayScale(GrayScaleImageBitmap grayScaleImageBitmap) {
        int width = grayScaleImageBitmap.getWidth();
        int height = grayScaleImageBitmap.getHeight();


        int[][] bitmap = grayScaleImageBitmap.getBitmap();

        int depth = 1;
        RGB[] colors = new RGB[width * height];


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                int gray = bitmap[i][j];

                colors[i * width + j] = new RGB(gray, gray, gray);

            }
        }

        PaletteData paletteData = new PaletteData(colors);

        ImageData imageData = new ImageData(width, height, depth, paletteData);


        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                int gray = bitmap[i][j];

                int rgb = new Color(gray, gray, gray).getRGB();

                int grayBytes = rgb;
                imageData.setPixel(i,j, grayBytes);

            }
        }

        return imageData;

    }
}
