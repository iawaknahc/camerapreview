package com.github.iawaknahc.camerapreview.example;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.iawaknahc.camerapreview.CameraPreviewView;
import com.github.iawaknahc.camerapreview.CameraUtil;
import com.github.iawaknahc.camerapreview.DeviceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity
        extends AppCompatActivity
        implements View.OnClickListener,
        Camera.PictureCallback {

    private static final String TAG = "CameraActivity";

    private ViewGroup cameraPreviewViewContainer;
    private CameraPreviewView cameraPreviewView;
    private TextView cameraRotationTextView;
    private Button buttonChangeCamera;
    private Button buttonCapture;
    private Camera camera;
    private int cameraId;
    private OrientationEventListener orientationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        cameraPreviewViewContainer = (ViewGroup) findViewById(R.id.camera_preview_view_container);
        cameraRotationTextView = (TextView) findViewById(R.id.camera_rotation_text_view);
        buttonChangeCamera = (Button) findViewById(R.id.button_change_camera);
        buttonCapture = (Button) findViewById(R.id.button_capture);

        buttonChangeCamera.setOnClickListener(this);
        buttonCapture.setOnClickListener(this);

        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                handleOnOrientationChange(orientation);
            }
        };
    }

    private void renderCameraRotationTextView(int cameraRotation) {
        cameraRotationTextView.setText("camera rotation: " + cameraRotation);
    }

    private void handleOnOrientationChange(int orientation) {
        if (camera == null) {
            return;
        }
        final int rotation = CameraUtil.calculateCameraRotation(cameraId, orientation);
        if (rotation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return;
        }
        renderCameraRotationTextView(rotation);
    }

    private void acquireCamera(int cameraId) {
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_FULLSCREEN
        );
        acquireResources();
    }

    private void acquireResources() {
        acquireCamera(cameraId);
        setupCameraPreviewView();
        orientationEventListener.enable();
    }

    private void releaseResources() {
        orientationEventListener.disable();
        removeCameraPreviewView();
        freeCamera();
    }

    private void setupCameraPreviewView() {
        cameraPreviewView = new CameraPreviewView(
                this,
                cameraId,
                camera
        );
        DisplayMetrics displayMetrics = DeviceUtil.getDisplayMetrics(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                displayMetrics.widthPixels,
                displayMetrics.widthPixels
        );
        cameraPreviewView.setLayoutParams(layoutParams);
        cameraPreviewView.initialize();
        cameraPreviewViewContainer.addView(cameraPreviewView);
    }

    private void removeCameraPreviewView() {
        if (cameraPreviewView != null) {
            cameraPreviewViewContainer.removeView(cameraPreviewView);
            cameraPreviewView = null;
        }
    }

    private void freeCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onPause() {
        releaseResources();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        if (viewId == R.id.button_change_camera) {
            handleChangeCamera();
        }
        if (viewId == R.id.button_capture) {
            handleCapture();
        }
    }

    private void handleChangeCamera() {
        releaseResources();
        cameraId = (cameraId + 1) % Camera.getNumberOfCameras();
        acquireResources();
    }

    private void handleCapture() {
        camera.takePicture(null, null, this);
    }

    private File writeToDisk(byte[] data) {
        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = File.createTempFile("camerainspector_", ".jpg", dir);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
            fileOutputStream.close();
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Toast.makeText(
                this,
                "pictureTaken when camera's ratotion is " + camera.getParameters().get("rotation"),
                Toast.LENGTH_LONG
        ).show();
        File file = writeToDisk(data);
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.setType("image/jpeg");
        intent.setData(Uri.fromFile(file));
        startActivity(intent);
    }
}
