package com.github.iawaknahc.camerapreview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.ViewGroup;

import java.io.IOException;

public class CameraPreviewView extends ViewGroup implements TextureView.SurfaceTextureListener {

    private static final String TAG = "CameraPreviewView";

    private int cameraId;
    private Camera camera;
    private OrientationEventListener orientationEventListener;
    private TextureView textureView;

    public CameraPreviewView(
            Context context,
            int cameraId,
            Camera camera) {
        super(context);
        this.cameraId = cameraId;
        this.camera = camera;
    }

    public void initialize() {
        initializeOrientationEventListener();
        initializeTextureView();
        initializeCameraPreviewSizeAndPictureSize();
    }

    private void initializeOrientationEventListener() {
        this.orientationEventListener = new OrientationEventListener(getContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                CameraUtil.handleOnOrientationChanged(
                        CameraPreviewView.this.cameraId,
                        CameraPreviewView.this.camera,
                        orientation
                );
            }
        };
    }

    private void initializeTextureView() {
        textureView = new TextureView(getContext());
        textureView.setSurfaceTextureListener(this);
        addView(textureView);
    }

    private void initializeCameraPreviewSizeAndPictureSize() {
        // set initial camera rotation and display orientation
        int currentDeviceOrientation = DeviceUtil.getDeviceCurrentOrientation(getContext());
        CameraUtil.handleOnOrientationChanged(cameraId, camera, currentDeviceOrientation);

        // set preview size
        Size viewSize = Size.fromLayoutParams(getLayoutParams());
        Size previewSize = CameraUtil.selectBestPreviewSize(camera, viewSize);
        Size pictureSize = CameraUtil.selectBestPictureSize(camera, previewSize);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
        parameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
        camera.setParameters(parameters);
    }

    private void validateLayoutParamDimension(int dimen) {
        if (    dimen == LayoutParams.FILL_PARENT ||
                dimen == LayoutParams.MATCH_PARENT ||
                dimen == LayoutParams.WRAP_CONTENT ||
                dimen <= 0) {
            throw new IllegalArgumentException("must be positive integer");
        }
    }

    private void validateLayoutParams(LayoutParams layoutParams) {
        validateLayoutParamDimension(layoutParams.width);
        validateLayoutParamDimension(layoutParams.height);
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

    @Override
    public void setLayoutParams(LayoutParams params) {
        validateLayoutParams(params);
        super.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Size correctedPreviewSize = CameraUtil.getCorrectlyPreviewSize(getContext(), cameraId, camera);
        textureView.measure(
                MeasureSpec.makeMeasureSpec(correctedPreviewSize.getWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(correctedPreviewSize.getHeight(), MeasureSpec.EXACTLY)
        );
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Size correctedPreviewSize = CameraUtil.getCorrectlyPreviewSize(getContext(), cameraId, camera);

        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();

        int leftOffset;
        int rightOffset;
        int topOffset;
        int bottomOffset;

        if (correctedPreviewSize.getWidth() > measuredWidth) {
            rightOffset = (correctedPreviewSize.getWidth() - measuredWidth) / 2;
            leftOffset = -rightOffset;
        } else {
            leftOffset = (measuredWidth - correctedPreviewSize.getWidth()) / 2;
            rightOffset = -leftOffset;
        }

        if (correctedPreviewSize.getHeight() > measuredHeight) {
            bottomOffset = (correctedPreviewSize.getHeight() - measuredHeight) / 2;
            topOffset = -bottomOffset;
        } else {
            topOffset = (measuredHeight - correctedPreviewSize.getHeight()) / 2;
            bottomOffset = -topOffset;
        }

        textureView.layout(
                leftOffset,
                topOffset,
                measuredWidth + rightOffset,
                measuredHeight + bottomOffset
        );
    }

    private void setPreviewOrientation() {
        CameraUtil.setCameraDisplayOrientationDuringInitialization(getContext(), cameraId, camera);
    }

    protected void stopPreview() {
        camera.stopPreview();
    }

    protected void startPreview(SurfaceTexture surfaceTexture) {
        try {
            setPreviewOrientation();
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        startPreview(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        stopPreview();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // no-op
    }

}
