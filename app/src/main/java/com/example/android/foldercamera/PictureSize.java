package com.example.android.foldercamera;

import android.hardware.Camera;
import android.util.Log;

import java.util.List;

/**
 * Created by smes on 2015/9/22.
 */
public class PictureSize {
    private final String TAG = "fc:pix_size";
    List<Camera.Size> sizes;

    public PictureSize(android.hardware.Camera.Parameters params) {
        sizes = params.getSupportedPictureSizes();
        // Iterate through all available resolutions and choose one.
        // The chosen resolution will be store  d in mSize.
        android.hardware.Camera.Size mSize;
        for (Camera.Size size : sizes) {
            Log.d(TAG, "Available resolution: " + size.width + " " + size.height);
            mSize = size;
        }
    }
    public Camera.Size getSize(int idx){
        return sizes.get(idx);
    }


}

