package com.github.iawaknahc.camerapreview;

import android.content.Context;
import android.hardware.Camera;

import java.util.LinkedHashSet;
import java.util.List;

public class StrictCameraPreviewMeasurer extends CameraPreviewMeasurer {

    public StrictCameraPreviewMeasurer(
            Context context,
            int cameraId,
            Camera camera) {
        super(context, cameraId, camera);
    }

    @Override
    public MeasurementResult measure(
            LinkedHashSet<AspectRatio> requiredAspectRatios,
            int widthMeasureSpec,
            int heightMeasureSpec) {
        return measure(
                requiredAspectRatios,
                widthMeasureSpec,
                heightMeasureSpec,
                false
        );
    }

    private MeasurementResult measure(
            LinkedHashSet<AspectRatio> requiredAspectRatios,
            int widthMeasureSpec,
            int heightMeasureSpec,
            boolean measureSpecSwapped) {
        final int deviceCurrentOrientation = getDeviceCurrentOrientationOrThrow();
        final int cameraNaturalOrientation = getCameraNaturalOrientationOrThrow();

        if (deviceCurrentOrientation != cameraNaturalOrientation && !measureSpecSwapped) {
            return measure(
                    requiredAspectRatios,
                    heightMeasureSpec,
                    widthMeasureSpec,
                    true
            );
        }

        for (AspectRatio aspectRatio : requiredAspectRatios) {
            List<Size> candidates = supportedPreviewSizes;
            candidates = filterByAspectRatio(aspectRatio, candidates);
            candidates = sortByWidthDesc(candidates);
            candidates = filterByMeasureSpecs(widthMeasureSpec, heightMeasureSpec, candidates);
            if (candidates.size() > 0) {
                Size previewSize = candidates.get(0);
                Size measuredSize = previewSize;
                if (measureSpecSwapped) {
                    measuredSize = new Size(measuredSize.getHeight(), measuredSize.getWidth());
                }
                return new MeasurementResult(measuredSize, previewSize);
            }
        }

        throw new IllegalArgumentException("cannot find suitable preview size");
    }

}
