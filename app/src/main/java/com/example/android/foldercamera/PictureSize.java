package com.example.android.foldercamera;

import android.hardware.Camera;
import android.util.Log;

import java.util.List;

/**
 * Created by smes on 2015/9/22.
 */
public class PictureSize {
    private final String TAG = "fc:pix_size";
    public static final int LARGE_SIZE = 1;
    public static final int MEDIUM_SIZE = 2;
    public static final int SMALL_SIZE = 3;

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

    public Camera.Size getResolution(int size_class){
        double resolution = 13e6;
        if(size_class == LARGE_SIZE)
            resolution = 13e6;
        else if(size_class == MEDIUM_SIZE)
            resolution = 8e6;
        else if(size_class == SMALL_SIZE)
            resolution = 3.1e6;
        Camera.Size size = null;
        for (Camera.Size s : sizes) {
            Log.d(TAG, "Available resolution: " + s.width + " " + s.height);
            if(s.width*s.height < resolution){
                size.width = s.width;
                size.height = s.height;
            }
        }
        return size;
    }

}

