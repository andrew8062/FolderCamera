package com.example.android.foldercamera;

import android.hardware.Camera;

/**
 * Created by Andrew on 10/2/2015.
 */
public  class CameraCallback {

    private final int ROTATION_HEAD_TO_LEFT = 0;
    private final int ROTATION_PROTRAIT = 90;
    private final int ROTATION_HEAD_TO_RIGHT = 180;
    OrientationChangeDetector mOrientationChangeDetector;
    PictureSave pictureSave;

    public CameraCallback(PictureSave pictureSave, OrientationChangeDetector orientationChangeDetector)
    {
        mOrientationChangeDetector = orientationChangeDetector;
        this.pictureSave = pictureSave;
    }

    public Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
//            Bitmap bmp = pictureSave.save(data);
//            imageView1.setImageBitmap(bmp);
            //需要手動重新startPreview，否則停在拍下的瞬間


            pictureSave.save(data);
            camera.startPreview();


        }

    };
    public Camera.AutoFocusCallback autoFocusCallback_without_takePicture = new Camera.AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean success, Camera camera) {

        }
    };


    public Camera.AutoFocusCallback autoFocusCallback_with_takePicture = new Camera.AutoFocusCallback() {

        public void onAutoFocus(boolean success, Camera camera) {

            if (success) {
                //對焦成功才拍照
                int orientation = mOrientationChangeDetector.getOrientation();
                switch(orientation){
                    case OrientationChangeDetector.HEAD_PORTRIAT:
                        orientation = ROTATION_PROTRAIT;
                        break;
                    case OrientationChangeDetector.HEAD_TO_LEFT:
                        orientation = ROTATION_HEAD_TO_LEFT;
                        break;
                    case OrientationChangeDetector.HEAD_TO_RIGHT:
                        orientation = ROTATION_HEAD_TO_RIGHT;
                        break;
                }
                Camera.Parameters params = camera.getParameters();
                params.setRotation(orientation);
                camera.setParameters(params);
                camera.takePicture(null, null, jpeg);
            }
        }
    };


}
