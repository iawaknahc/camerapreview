package com.github.iawaknahc.camerapreview;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.OrientationEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CameraUtil {

    private static final String TAG = "CameraUtil";

    /**
     * Calculate the rotation for Camera.Parameters#setRotation
     *
     * @param cameraId
     * @param deviceOrientation in degrees
     * @return the rotation for Camera.Parameters#setRotation, or
     * OrientationEventListener.ORIENTATION_UNKNOWN
     */
    public static int calculateCameraRotation(int cameraId, int deviceOrientation) {
        if (deviceOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return OrientationEventListener.ORIENTATION_UNKNOWN;
        }
        Camera.CameraInfo cameraInfo = getCameraInfo(cameraId);
        final int rounded = Rounding.roundToMultiplesOf(90, deviceOrientation);
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (cameraInfo.orientation - rounded + 360) % 360;
        }
        return (cameraInfo.orientation + rounded) % 360;
    }

    public static int calculateCameraDisplayOrientation(int cameraId, int deviceOrientation) {
        if (deviceOrientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            return OrientationEventListener.ORIENTATION_UNKNOWN;
        }
        Camera.CameraInfo cameraInfo = getCameraInfo(cameraId);
        final int rounded = Rounding.roundToMultiplesOf(90, deviceOrientation);
        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + rounded) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - rounded + 360) % 360;
        }
        return result;
    }

    public static int calculateCameraDisplayOrientationForCurrentDeviceRotation(Context context, int cameraId) {
        int currentRotation = DeviceUtil.getCurrentRotationInDegrees(context);
        return calculateCameraDisplayOrientation(cameraId, currentRotation);
    }

    public static void setCameraDisplayOrientationDuringInitialization(Context context, int cameraId, Camera camera) {
        final int result = calculateCameraDisplayOrientationForCurrentDeviceRotation(context, cameraId);
        camera.setDisplayOrientation(result);
    }

    public static void handleOnOrientationChanged(
            int cameraId,
            Camera camera,
            int orientation) {
        final int cameraRotation = calculateCameraRotation(cameraId, orientation);
        if (cameraRotation != OrientationEventListener.ORIENTATION_UNKNOWN) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setRotation(cameraRotation);
            camera.setParameters(parameters);
        }
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

    public static boolean isCameraOrientationAlignedWithCurrentDeviceOrientation(Context context, int cameraId) {
        final int cameraNaturalOrientation = getCameraNaturalOrientation(context, cameraId);
        final int currentViewOrientation = DeviceUtil.getDeviceCurrentOrientation(context);
        return cameraNaturalOrientation == currentViewOrientation;
    }

    public static Size getCorrectlyPreviewSize(Context context, int cameraId, Camera camera) {
        Size previewSize = CameraUtil.toSize(camera.getParameters().getPreviewSize());
        if (!CameraUtil.isCameraOrientationAlignedWithCurrentDeviceOrientation(context, cameraId)) {
            previewSize = previewSize.swap();
        }
        return previewSize;
    }

    public static Size getBestByArea(Size predicate, Collection<Size> sizes) {
        final int area = predicate.getArea();
        Size best = null;
        int diff = Integer.MAX_VALUE;
        for (Size size : sizes) {
            if (size.getWidth() == predicate.getWidth() ||
                    size.getHeight() == predicate.getHeight() ||
                    size.getWidth() == predicate.getHeight() ||
                    size.getHeight() == predicate.getWidth()) {
                return size;
            }
            int areaDiff = Math.abs(area - size.getArea());
            if (areaDiff < diff) {
                diff = areaDiff;
                best = size;
            }
        }
        return best;
    }

    public static Size selectBestPreviewSize(Camera camera, Size viewSize) {
        List<Size> supportedPreviewSizes = toSizes(camera.getParameters().getSupportedPreviewSizes());
        List<Size> supportedPictureSizes = toSizes(camera.getParameters().getSupportedPictureSizes());

        Set<Size> set1 = new HashSet<>(supportedPreviewSizes);
        Set<Size> set2 = new HashSet<>(supportedPictureSizes);
        Set<Size> set3 = new HashSet<>(set1);
        set3.retainAll(set2);

        return getBestByArea(viewSize, set3);
    }

    public static Size selectBestPictureSize(Camera camera, Size previewSize) {
        AspectRatio previewSizeAspectRatio = AspectRatio.fromSize(previewSize);
        List<Size> supportedPictureSizes = toSizes(camera.getParameters().getSupportedPictureSizes());
        List<Size> sameAspectRatio = new ArrayList<>();
        for (Size size : supportedPictureSizes) {
            AspectRatio as1  = AspectRatio.fromSize(size);
            if (previewSizeAspectRatio.equals(as1)) {
                sameAspectRatio.add(size);
            }
        }
        return getBestByArea(previewSize, sameAspectRatio);
    }

}
