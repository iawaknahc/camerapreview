package com.github.iawaknahc.camerapreview;

import android.view.ViewGroup;

public final class Size {

    private final int width;
    private final int height;

    public static Size fromLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new Size(layoutParams.width, layoutParams.height);
    }

    public Size(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("must be non negative integer");
        }
        this.width = width;
        this.height = height;
    }

    public Size swap() {
        return new Size(height, width);
    }

    public int getArea() {
        return width * height;
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Size size = (Size) o;

        if (width != size.width) return false;
        return height == size.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

}
