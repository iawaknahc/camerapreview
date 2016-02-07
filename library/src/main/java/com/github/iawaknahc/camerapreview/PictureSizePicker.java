package com.github.iawaknahc.camerapreview;

import android.hardware.Camera;

import java.util.List;

public abstract class PictureSizePicker {

    protected final List<Size> supportedPictureSizes;

    public PictureSizePicker(Camera camera) {
        supportedPictureSizes = CameraUtil.toSizes(
                camera.getParameters().getSupportedPictureSizes()
        );
    }

    public Size pickByPreviewSize(Size previewSize) {
        List<Size> sortedPictureSizes = CameraPreviewMeasurer.sortByWidthDesc(
                CameraPreviewMeasurer.filterByAspectRatio(
                        AspectRatio.fromSize(previewSize),
                        supportedPictureSizes
                )
        );
        return pickFromSortedPictureSizes(previewSize, sortedPictureSizes);
    }

    protected abstract Size pickFromSortedPictureSizes(Size previewSize, List<Size> sizes);

}
