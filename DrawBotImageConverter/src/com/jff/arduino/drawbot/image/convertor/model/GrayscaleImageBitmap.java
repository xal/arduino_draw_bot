package com.jff.arduino.drawbot.image.convertor.model;

public class GrayScaleImageBitmap {


    private int [][] bitmap = new int[0][0];

    public GrayScaleImageBitmap(int[][] bitmap) {
        this.bitmap = bitmap;
    }

    public int getWidth() {

        return bitmap.length;
    }
    public int getHeight() {

        return bitmap[0].length;
    }

    public int[][] getBitmap() {
        return bitmap;
    }

}
