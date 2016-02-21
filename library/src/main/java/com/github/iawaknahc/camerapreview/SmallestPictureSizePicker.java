package com.github.iawaknahc.camerapreview;

import android.hardware.Camera;

import java.util.List;

public class SmallestPictureSizePicker extends PictureSizePicker {

    public SmallestPictureSizePicker(Camera camera) {
        super(camera);
    }

    @Override
    protected Size pickFromSortedPictureSizes(Size previewSize, List<Size> sizes) {
        return sizes.get(sizes.size() - 1);
    }

}
