package com.github.iawaknahc.camerapreview;

import junit.framework.Assert;

import org.junit.Test;

import com.github.iawaknahc.camerapreview.Rounding;

public class RoundingTest {

    @Test
    public void testRoundToMultiplesOf() {
        for (int i = 0; i < 45; ++i) {
            Assert.assertEquals(0, Rounding.roundToMultiplesOf(90, i));
        }
        for (int i = 45; i < 90 + 45; ++i) {
            Assert.assertEquals(90, Rounding.roundToMultiplesOf(90, i));
        }
        for (int i = 90 + 45; i < 180 + 45; ++i) {
            Assert.assertEquals(180, Rounding.roundToMultiplesOf(90, i));
        }
        for (int i = 180 + 45; i < 270 + 45; ++i) {
            Assert.assertEquals(270, Rounding.roundToMultiplesOf(90, i));
        }
        for (int i = 270 + 45; i < 360; ++i) {
            Assert.assertEquals(360, Rounding.roundToMultiplesOf(90, i));
        }
    }
}
