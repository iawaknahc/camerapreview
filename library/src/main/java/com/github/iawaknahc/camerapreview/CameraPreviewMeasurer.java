package com.github.iawaknahc.camerapreview;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class CameraPreviewMeasurer {

    protected final Context context;
    protected final int cameraId;
    protected final List<Size> supportedPreviewSizes;

    public CameraPreviewMeasurer(Context context, int cameraId, Camera camera) {
        this.context = context;
        this.cameraId = cameraId;
        this.supportedPreviewSizes = CameraUtil.toSizes(camera.getParameters().getSupportedPreviewSizes());
    }

    public abstract MeasurementResult measure(
            LinkedHashSet<AspectRatio> requiredAspectRatios,
            int widthMeasureSpec,
            int heightMeasureSpec
    );

    protected int getDeviceCurrentOrientationOrThrow() {
        final int deviceCurrentOrientation = DeviceUtil.getDeviceCurrentOrientation(context);
        if (deviceCurrentOrientation == Configuration.ORIENTATION_UNDEFINED) {
            throw new IllegalStateException("device current orientation is undefined");
        }
        return deviceCurrentOrientation;
    }

    protected int getCameraNaturalOrientationOrThrow() {
        final int cameraNaturalOrientation = CameraUtil.getCameraNaturalOrientation(context, cameraId);
        if (cameraNaturalOrientation == Configuration.ORIENTATION_UNDEFINED) {
            throw new IllegalStateException("camera " + cameraId + " natural orientation is undefined");
        }
        return cameraNaturalOrientation;
    }

    public static List<Size> filterByAtMostWidth(
            int width,
            List<Size> sizes) {
        List<Size> output = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getWidth() <= width) {
                output.add(size);
            }
        }
        return output;
    }

    public static List<Size> filterByAtMostHeight(
            int height,
            List<Size> sizes) {
        List<Size> output = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getHeight() <= height) {
                output.add(size);
            }
        }
        return output;
    }

    public static List<Size> filterByExactWidth(
            int width,
            List<Size> sizes) {
        List<Size> output = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getWidth() == width) {
                output.add(size);
            }
        }
        return output;
    }

    public static List<Size> filterByExactHeight(
            int height,
            List<Size> sizes) {
        List<Size> output = new ArrayList<>();
        for (Size size : sizes) {
            if (size.getHeight() == height) {
                output.add(size);
            }
        }
        return output;
    }

    public static List<Size> filterByAspectRatio(
            AspectRatio aspectRatio,
            List<Size> sizes) {
        List<Size> output = new ArrayList<>();
        for (Size size : sizes) {
            AspectRatio thisAspectRatio = AspectRatio.fromSize(size);
            if (thisAspectRatio.equals(aspectRatio)) {
                output.add(size);
            }
        }
        return output;
    }

    public static List<Size> sortByWidthDesc(List<Size> sizes) {
        List<Size> output = new ArrayList<>(sizes);
        Collections.sort(output, new Comparator<Size>() {
            @Override
            public int compare(Size lhs, Size rhs) {
                return Sorting.reverseCompareOrder(Sorting.compareInt(lhs.getWidth(), rhs.getWidth()));
            }
        });
        return output;
    }

    public static List<Size> filterByMeasureSpecs(
            int widthMeasureSpec,
            int heightMeasureSpec,
            List<Size> sizes) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == View.MeasureSpec.AT_MOST) {
            sizes = filterByAtMostWidth(width, sizes);
        }
        if (heightMode == View.MeasureSpec.AT_MOST) {
            sizes = filterByAtMostHeight(height, sizes);
        }
        if (widthMode == View.MeasureSpec.EXACTLY) {
            sizes = filterByExactWidth(width, sizes);
        }
        if (heightMode == View.MeasureSpec.EXACTLY) {
            sizes = filterByExactHeight(height, sizes);
        }
        return sizes;
    }

}
