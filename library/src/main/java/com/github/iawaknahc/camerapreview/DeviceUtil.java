package com.github.iawaknahc.camerapreview;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

public class DeviceUtil {

    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager windowManager = getWindowManager(context);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int getCurrentRotationInDegrees(Context context) {
        final WindowManager windowManager = getWindowManager(context);
        final int currentRotation = windowManager.getDefaultDisplay().getRotation();

        switch (currentRotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                throw new IllegalStateException("the device current rotation is neither 0, 90, 180 nor 270");
        }
    }

    public static int getDeviceCurrentOrientation(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation != Configuration.ORIENTATION_LANDSCAPE
                && orientation != Configuration.ORIENTATION_PORTRAIT) {
            return Configuration.ORIENTATION_UNDEFINED;
        }
        return orientation;
    }

    public static int getDeviceNaturalOrientation(Context context) {
        final int currentOrientation = getDeviceCurrentOrientation(context);

        if (currentOrientation == Configuration.ORIENTATION_UNDEFINED) {
            return Configuration.ORIENTATION_UNDEFINED;
        }

        final WindowManager windowManager = getWindowManager(context);
        final int currentRotation = windowManager.getDefaultDisplay().getRotation();

        if (currentRotation != Surface.ROTATION_0 &&
                currentRotation != Surface.ROTATION_90 &&
                currentRotation != Surface.ROTATION_180 &&
                currentRotation != Surface.ROTATION_270) {
            return Configuration.ORIENTATION_UNDEFINED;
        }

        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            if (currentRotation == Surface.ROTATION_0 || currentRotation == Surface.ROTATION_180) {
                return Configuration.ORIENTATION_PORTRAIT;
            }
            return Configuration.ORIENTATION_LANDSCAPE;
        }

        if (currentRotation == Surface.ROTATION_0 || currentRotation == Surface.ROTATION_180) {
            return Configuration.ORIENTATION_LANDSCAPE;
        }

        return Configuration.ORIENTATION_PORTRAIT;
    }

    private static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

}
