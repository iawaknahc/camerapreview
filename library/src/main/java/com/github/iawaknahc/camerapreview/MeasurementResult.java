package com.github.iawaknahc.camerapreview;

public class MeasurementResult {

    private final Size previewSize;
    private final Size measuredSize;

    public MeasurementResult(Size measuredSize, Size previewSize) {
        this.measuredSize = measuredSize;
        this.previewSize = previewSize;
    }

    public Size getPreviewSize() {
        return previewSize;
    }

    public Size getMeasuredSize() {
        return measuredSize;
    }

}
