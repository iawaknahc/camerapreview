package com.github.iawaknahc.camerapreview;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.LinkedHashSet;

public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreviewView";

    private final int cameraId;
    private final Camera camera;
    private final OrientationEventListener orientationEventListener;
    private final LinkedHashSet<AspectRatio> requiredAspectRatios;
    private final CameraPreviewMeasurer cameraPreviewMeasurer;
    private final PictureSizePicker pictureSizePicker;
    private boolean isSurfaceCreated;
    private boolean isMeasuredAtLeastOnce;
    private boolean isPreviewStarted;

    public CameraPreviewView(
            Context context,
            int cameraId,
            Camera camera,
            LinkedHashSet<AspectRatio> requiredAspectRatios,
            CameraPreviewMeasurer cameraPreviewMeasurer,
            PictureSizePicker pictureSizePicker) {
        super(context);
        this.cameraId = cameraId;
        this.camera = camera;
        this.requiredAspectRatios = requiredAspectRatios;
        this.cameraPreviewMeasurer = cameraPreviewMeasurer;
        this.pictureSizePicker = pictureSizePicker;
        orientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int orientation) {
                handleOnOrientationChanged(orientation);
            }
        };
        getHolder().addCallback(this);
    }

    private void handleOnOrientationChanged(int orientation) {
        final int rotation = CameraUtil.onOrientationChanged(cameraId, orientation);
        if (rotation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setRotation(rotation);
        camera.setParameters(parameters);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        orientationEventListener.enable();
    }

    @Override
    protected void onDetachedFromWindow() {
        orientationEventListener.disable();
        super.onDetachedFromWindow();
    }

    private void setPreviewOrientation() {
        final int result = CameraUtil.calculateDisplayOrientation(getContext(), cameraId);
        camera.setDisplayOrientation(result);
    }

    private void stopPreview() {
        try {
            isPreviewStarted = false;
            camera.stopPreview();
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    private void startPreviewIfReady(SurfaceHolder holder) {
        if (isPreviewStarted || !isSurfaceCreated || !isMeasuredAtLeastOnce) {
            return;
        }
        try {
            setPreviewOrientation();
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            isPreviewStarted = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        isMeasuredAtLeastOnce = true;

        MeasurementResult measurementResult = cameraPreviewMeasurer.measure(
                requiredAspectRatios,
                widthMeasureSpec,
                heightMeasureSpec
        );

        Size previewSize = measurementResult.getPreviewSize();
        Size pictureSize = pictureSizePicker.pickByPreviewSize(previewSize);

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
        parameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
        camera.setParameters(parameters);

        setMeasuredDimension(
                measurementResult.getMeasuredSize().getWidth(),
                measurementResult.getMeasuredSize().getHeight()
        );

        startPreviewIfReady(getHolder());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceCreated = true;
        startPreviewIfReady(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        stopPreview();
        startPreviewIfReady(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }

}
