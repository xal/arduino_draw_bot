package com.jff.arduino.drawbot.image.convertor.model;

public enum EngineState {
    RIGHT_ANTICLOCKWISE(0),
    LEFT_ANTICLOCKWISE(1),
    RIGHT_CLOCKWISE(2),
    LEFT_CLOCKWISE(3);

    int byteChar;

    private EngineState(int byteChar) {
        this.byteChar = byteChar;
    }

    public String toString() {

        String string = null;

        if (this == RIGHT_ANTICLOCKWISE) {

            string = "RightMotorAntiClockWise();";


        } else if (this == RIGHT_CLOCKWISE) {

            string = "RightMotorClockWise();";
        } else if (this == LEFT_ANTICLOCKWISE) {

            string = "LeftMotorAntiClockWise();";
        } else if (this == LEFT_CLOCKWISE) {
            string = "LeftMotorClockWise();";

        }

        return string;
    }


    public String getByteChar() {

        String str = String.valueOf(byteChar);

        return str;
    }
}
