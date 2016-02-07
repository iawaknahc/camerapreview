package com.github.iawaknahc.camerapreview;

import junit.framework.Assert;

import org.junit.Test;

import java.util.LinkedHashSet;

import com.github.iawaknahc.camerapreview.Sorting;

public class SortingTest {

    @Test
    public void testCompareInt() {
        Assert.assertEquals(-1, Sorting.compareInt(0, 1));
        Assert.assertEquals(1, Sorting.compareInt(1, 0));
        Assert.assertEquals(0, Sorting.compareInt(0, 0));
    }

    @Test
    public void testReverseCompareOrder() {
        Assert.assertEquals(-1, Sorting.reverseCompareOrder(1));
        Assert.assertEquals(0, Sorting.reverseCompareOrder(0));
        Assert.assertEquals(1, Sorting.reverseCompareOrder(-1));
    }

    @Test
    public void testCompareWithPriorities() {
        LinkedHashSet<Integer> priorities = new LinkedHashSet<>();
        priorities.add(6);
        priorities.add(3);

        Assert.assertEquals(0, Sorting.compareWithPriorities(priorities, 0, 1));
        Assert.assertEquals(0, Sorting.compareWithPriorities(priorities, 1, 0));
        Assert.assertEquals(-1, Sorting.compareWithPriorities(priorities, 6, 0));
        Assert.assertEquals(1, Sorting.compareWithPriorities(priorities, 0, 6));
        Assert.assertEquals(-1, Sorting.compareWithPriorities(priorities, 3, 0));
        Assert.assertEquals(1, Sorting.compareWithPriorities(priorities, 0, 3));
    }

}
