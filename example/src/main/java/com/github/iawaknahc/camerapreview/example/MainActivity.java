package com.github.iawaknahc.camerapreview.example;

import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.iawaknahc.camerapreview.AspectRatio;
import com.github.iawaknahc.camerapreview.DeviceUtil;
import com.github.iawaknahc.camerapreview.Size;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView cameraInspectResultView;

    private Button buttonLaunchCamera;

    private OrientationEventListener orientationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraInspectResultView = (TextView) findViewById(R.id.camera_inspection_result);
        buttonLaunchCamera = (Button) findViewById(R.id.button_launch_camera);
        buttonLaunchCamera.setOnClickListener(this);
        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                Log.d(TAG, "orientation=" + orientation);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationEventListener.enable();
        String inspectResult = "";

        inspectResult += "getResources().getConfiguration().orientation=";
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_UNDEFINED:
                inspectResult += "Configuration.ORIENTATION_UNDEFINED";
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                inspectResult += "Configuration.ORIENTATION_PORTRAIT";
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                inspectResult += "Configuration.ORIENTATION_LANDSCAPE";
                break;
            case Configuration.ORIENTATION_SQUARE:
                inspectResult += "Configuration.ORIENTATION_SQUARE";
                break;
            default:
                throw new AssertionError();
        }
        inspectResult += "\n\n";

        inspectResult += "getWindowManager().getDefaultDisplay().getRotation()=";
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                inspectResult += "Surface.ROTATION_0";
                break;
            case Surface.ROTATION_90:
                inspectResult += "Surface.ROTATION_90";
                break;
            case Surface.ROTATION_180:
                inspectResult += "Surface.ROTATION_180";
                break;
            case Surface.ROTATION_270:
                inspectResult += "Surface.ROTATION_270";
                break;
            default:
                throw new AssertionError();
        }
        inspectResult += "\n\n";

        DisplayMetrics displayMetrics = DeviceUtil.getDisplayMetrics(this);
        inspectResult += "displayMetrics.widthPixels=" + displayMetrics.widthPixels + ", displayMetrics.heightPixels=" + displayMetrics.heightPixels;
        inspectResult += "\n\n";

        final int numberOfCameras = Camera.getNumberOfCameras();
        inspectResult += "numberOfCameras=" + numberOfCameras + "\n\n";

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; ++i) {
            Camera.getCameraInfo(i, cameraInfo);
            inspectResult += "camera " + i + "\n";
            inspectResult += "orientation=" + cameraInfo.orientation + "\n";
            inspectResult += "facing=" + cameraInfo.facing + "\n";
            inspectResult += concatCameraParameters(i);
        }

        cameraInspectResultView.setText(inspectResult);
    }

    @Override
    protected void onPause() {
        orientationEventListener.disable();
        super.onPause();
    }

    String concatCameraParameters(int cameraId) {
        Camera camera = null;
        try {
            camera = Camera.open(cameraId);
            String result = "";
            Camera.Parameters parameters = camera.getParameters();

            result += "supported picture sizes:\n";
            for (Camera.Size size : parameters.getSupportedPictureSizes()) {
                final int w = size.width;
                final int h = size.height;
                result += "w=" + w + ", h=" + h + ", aspectRatio=" + AspectRatio.fromSize(new Size(w, h)) + "\n";
            }

            result += "supported preview sizes:\n";
            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
                final int w = size.width;
                final int h = size.height;
                result += "w=" + w + ", h=" + h + ", aspectRatio=" + AspectRatio.fromSize(new Size(w, h)) + "\n";
            }
            result += "\n";

            return result;
        } catch (RuntimeException e) {
            String message = "cannot open camera " + cameraId + "\n";
            Log.e(TAG, message, e);
            return message;
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_launch_camera) {
            startActivity(new Intent(this, CameraActivity.class));
        }
    }
}
