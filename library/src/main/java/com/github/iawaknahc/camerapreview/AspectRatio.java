package com.github.iawaknahc.camerapreview;

public class AspectRatio {

    public static final AspectRatio _3_2 = new AspectRatio(3, 2);
    public static final AspectRatio _4_3 = new AspectRatio(4, 3);
    public static final AspectRatio _16_9 = new AspectRatio(16, 9);

    private final int normalizedWidth;
    private final int normalizedHeight;

    private AspectRatio(Size size) {
        this(size.getWidth(), size.getHeight());
    }

    private AspectRatio(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
        final int gcd = calculateGcd(width, height);
        this.normalizedWidth = width / gcd;
        this.normalizedHeight = height / gcd;
    }

    public static AspectRatio fromSize(Size size) {
        return new AspectRatio(size);
    }

    public static int calculateGcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return calculateGcd(b, a % b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AspectRatio that = (AspectRatio) o;

        if (normalizedWidth != that.normalizedWidth) return false;
        return normalizedHeight == that.normalizedHeight;

    }

    @Override
    public int hashCode() {
        int result = normalizedWidth;
        result = 31 * result + normalizedHeight;
        return result;
    }

    @Override
    public String toString() {
        return normalizedWidth + ":" + normalizedHeight;
    }

}
