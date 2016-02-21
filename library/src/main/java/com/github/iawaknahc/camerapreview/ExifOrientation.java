package com.github.iawaknahc.camerapreview;

import android.graphics.Matrix;

public enum ExifOrientation {

    UNDEFINED,
    NORMAL,
    FLIP_HORIZONTAL,
    ROTATE_180,
    FLIP_VERTICAL,
    TRANSPOSE,
    ROTATE_90,
    TRANSVERSE,
    ROTATE_270;

    public static ExifOrientation fromExifValue(int value) {
        ExifOrientation[] values = ExifOrientation.values();
        if (value < 0 || value >= values.length) {
            throw new IllegalArgumentException(
                    "the value of exif orientation is in the range [0," + values.length + "]"
            );
        }
        return values[value];
    }

    public Matrix createTransformationMatrix() {
        Matrix transformationMatrix = new Matrix();
        switch (this) {
            case FLIP_HORIZONTAL:
                transformationMatrix.postScale(-1f, 1f);
                break;
            case ROTATE_180:
                transformationMatrix.postRotate(180f);
                break;
            case FLIP_VERTICAL:
                transformationMatrix.postScale(1f, -1f);
                break;
            case TRANSPOSE:
                transformationMatrix.postRotate(90f);
                transformationMatrix.postScale(-1f, 1f);
                break;
            case ROTATE_90:
                transformationMatrix.postRotate(90f);
                break;
            case TRANSVERSE:
                transformationMatrix.postRotate(270f);
                transformationMatrix.postScale(-1f, 1f);
                break;
            case ROTATE_270:
                transformationMatrix.postRotate(270f);
                break;
            case UNDEFINED:
            case NORMAL:
                // no-op
                break;
            default:
                throw new AssertionError();
        }
        return transformationMatrix;
    }
}
