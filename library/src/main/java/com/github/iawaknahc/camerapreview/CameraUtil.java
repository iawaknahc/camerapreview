package com.github.iawaknahc.camerapreview;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.OrientationEventListener;

import java.util.ArrayList;
import java.util.List;

public class CameraUtil {

    /**
     * Calculate the orientation for Camera.Parameters#setRotation
     *
     * @param cameraId
     * @param orientation, must be the raw value from OrientationEventListener#onOrientationChanged
     * @return the rotation for Camera.Parameters#setRotation, or
     * OrientationEventListener.ORIENTATION_UNKNOWN
     */
    public static int onOrientationChanged(int cameraId, int orientation) {
        Camera.CameraInfo cameraInfo = getCameraInfo(cameraId);
        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return OrientationEventListener.ORIENTATION_UNKNOWN;
        }
        final int rounded = Rounding.roundToMultiplesOf(90, orientation);
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (cameraInfo.orientation - rounded + 360) % 360;
        }
        return (cameraInfo.orientation + rounded) % 360;
    }

    public static Size toSize(Camera.Size cameraSize) {
        return new Size(cameraSize.width, cameraSize.height);
    }

    public static List<Size> toSizes(List<Camera.Size> cameraSizes) {
        List<Size> sizes = new ArrayList<>(cameraSizes.size());
        for (Camera.Size cameraSize : cameraSizes) {
            sizes.add(toSize(cameraSize));
        }
        return sizes;
    }

    public static Camera.CameraInfo getCameraInfo(int cameraId) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        return cameraInfo;
    }

    public static int getCameraNaturalOrientation(Context context, int cameraId) {
        final int deviceNaturalOrientation = DeviceUtil.getDeviceNaturalOrientation(context);

        if (deviceNaturalOrientation == Configuration.ORIENTATION_UNDEFINED) {
            return Configuration.ORIENTATION_UNDEFINED;
        }

        final Camera.CameraInfo cameraInfo = getCameraInfo(cameraId);
        final int relativeCameraOrientation = cameraInfo.orientation;

        if (deviceNaturalOrientation == Configuration.ORIENTATION_PORTRAIT) {
            if (relativeCameraOrientation == 0 || relativeCameraOrientation == 180) {
                return Configuration.ORIENTATION_PORTRAIT;
            }
            return Configuration.ORIENTATION_LANDSCAPE;
        }

        if (relativeCameraOrientation == 0 || relativeCameraOrientation == 180) {
            return Configuration.ORIENTATION_LANDSCAPE;
        }
        return Configuration.ORIENTATION_PORTRAIT;
    }

    public static int calculateDisplayOrientation(Context context, int cameraId) {
        Camera.CameraInfo cameraInfo = getCameraInfo(cameraId);
        int currentRotation = DeviceUtil.getCurrentRotationInDegrees(context);

        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + currentRotation) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - currentRotation + 360) % 360;
        }
        return result;
    }

}
