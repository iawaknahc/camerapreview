package com.github.iawaknahc.camerapreview;

public class Rounding {
    public static int roundToMultiplesOf(int interval, int value) {
        return (value + interval / 2) / interval * interval;
    }
}
