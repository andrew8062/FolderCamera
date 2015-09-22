package com.example.android.foldercamera;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;


import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;


public class MainActivity extends Activity implements SurfaceHolder.Callback {
    public static final String SHARED_PREFERENCE_KEY = "shared_preference";
    public static final int ORIENTAION_CHANGE = 1;
    private static final String TAG = "fc";
    CustomDialog customDialog;
    OrientationChangeDetector orientationChangeDetector;
    PictureSave pictureSave;
    int currentZoomLevel;
    double finger_distance = 0;
    PictureCallback jpeg = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
//            Bitmap bmp = pictureSave.save(data);
//            imageView1.setImageBitmap(bmp);
            //需要手動重新startPreview，否則停在拍下的瞬間


            pictureSave.save(data);
            camera.startPreview();

        }

    };
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case ORIENTAION_CHANGE:
//                    int[] degrees = getButtonRotationDegree((int)take_picture_button.getTag(), msg.arg1);
//                    RotateAnimation rotateAnimation = new RotateAnimation(degrees[0], degrees[1], Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                            0.5f);
//                    rotateAnimation.setDuration(300);
//                    rotateAnimation.setFillAfter(true);
////                    rotateAnimation.setFillEnabled(true);
//                    if (msg.arg1 !=  orientation) {
//                        orientation = msg.arg1;
//                        take_picture_button.startAnimation(rotateAnimation);
//                        take_picture_button.setTag(orientation);
//                    }
//                    break;
//            }
        }
    };
    private Camera camera;
    private Camera.Parameters parameters;
    private Camera.CameraInfo info;
    //自動對焦監聽式
    Camera.AutoFocusCallback afcb = new Camera.AutoFocusCallback() {

        public void onAutoFocus(boolean success, Camera camera) {

            if (success) {
                //對焦成功才拍照
                int orientation = orientationChangeDetector.getOrientation();
                switch(orientation){
                    case 0:
                        orientation = 90;
                        break;
                    case 1:
                        orientation = 0;
                        break;
                    case 2:
                        orientation = 180;
                        break;
                }
                Camera.Parameters params = camera.getParameters();
                params.setRotation(orientation);
                camera.setParameters(params);
                camera.takePicture(null, null, jpeg);

            }
        }
    };
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button take_picture_button;
    //image view for review
    private ImageView imageView1;
    private int orientation = -1;
    private CircleButton fam;
    private View.OnClickListener famOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "famisclicked");
            //folderDialog.showDialog()
            // //Here's the magic..
            //Set the dialog to not focusable (makes navigation ignore us adding the window)
            customDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            //Show the dialog!
            customDialog.show();
            //Set the dialog to immersive
            customDialog.getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility());
            //Clear the not focusable flag from the window
            customDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            ;
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = Camera.open();
        fam = (CircleButton) findViewById(R.id.float_button);
        fam.setOnClickListener(famOnClickListener);
        setPictureSize();
        setup_custom_dialog();
        setup_tack_picture_button();
        orientationChangeDetector = new OrientationChangeDetector(this, mHandler);
        pictureSave = new PictureSave();

        surfaceView = (SurfaceView)
                findViewById(R.id.surfaceView);

        imageView1 = (ImageView)
                findViewById(R.id.imageView1);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);


    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setImmersiveMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        orientationChangeDetector.unregister();
    }
    private void setup_custom_dialog(){
        customDialog = new CustomDialog(MainActivity.this, pictureSave);
        customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setImmersiveMode();
            }
        });
    }
    private void setup_tack_picture_button() {
        take_picture_button = (Button) findViewById(R.id.button1);
        take_picture_button.setTag(0);
        take_picture_button.setOnClickListener(new View.OnClickListener() {

                                                   public void onClick(View v) {
                                                       //自動對焦
                                                       camera.autoFocus(afcb);
                                                   }
                                               }

        );
    }
    private void setPictureSize() {
        parameters = camera.getParameters();
        PictureSize pictureSave = new PictureSize(parameters);
        Camera.Size size = pictureSave.getSize(0);
        parameters.setPictureSize(size.width, size.height);
        camera.setParameters(parameters);

    }
    private void setImmersiveMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }





    private void setupZoomControl() {
        ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomControls);
        if (parameters.isZoomSupported()) {
            final int maxZoomLevel = parameters.getMaxZoom();
            currentZoomLevel = 0;
            zoomControls.setIsZoomInEnabled(true);
            zoomControls.setIsZoomOutEnabled(true);
            zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          zoomCamera(true);
                                                      }
                                                  }

            );
            zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           zoomCamera(false);
                                                       }
                                                   }

            );
        } else {
            zoomControls.setVisibility(View.GONE);
        }
    }

    public void zoomCamera(boolean zoomInOrOut) {
        if (camera != null) {
            Camera.Parameters parameter = camera.getParameters();

            if (parameter.isZoomSupported()) {
                int MAX_ZOOM = parameter.getMaxZoom();
                int currentZoom = parameter.getZoom();
                if (zoomInOrOut && (currentZoom < MAX_ZOOM && currentZoom >= 0)) {
                    currentZoom = currentZoom + 3;
                    parameter.setZoom(currentZoom);
                } else if (!zoomInOrOut && (currentZoom <= MAX_ZOOM && currentZoom > 0)) {
                    currentZoom = currentZoom - 3;
                    parameter.setZoom(currentZoom);
                }
            } else
                Toast.makeText(getApplicationContext(), "Zoom Not Avaliable", Toast.LENGTH_LONG).show();

            camera.setParameters(parameter);
        }
    }

    private double handleZoomGesture(MotionEvent event, double mDist) {
        double newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            zoomCamera(true);
        } else if (newDist < mDist) {
            zoomCamera(false);
        }
        return newDist;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void surfaceCreated(SurfaceHolder holder) {

        //camera = Camera.open();
        parameters = camera.getParameters();
        try {

            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

            //looking for resolution
            for (int i = 0; i < sizes.size(); i++) {
                Log.d("fc:", sizes.get(i).height + " " + sizes.get(i).width);
            }
            Camera.Size cs = sizes.get(0);
            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setPreviewSize(cs.width, cs.height);
            camera.setParameters(parameters);


            //設置參數
            camera.setPreviewDisplay(surfaceHolder);
            //setup zoomcontrol
            setupZoomControl();

//            鏡頭的方向和手機相差90度，所以要轉向
            camera.setDisplayOrientation(90);

            //攝影頭畫面顯示在Surface上
            camera.startPreview();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {

        System.out.println("surfaceDestroyed");
        camera.stopPreview();
        //關閉預覽
        camera.release();
        //
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        Log.d(TAG, "pointer count: " + event.getPointerCount());
//        if (action == MotionEvent.ACTION_DOWN) {
//            camera.autoFocus(afcb);
//        }
        if (event.getPointerCount() > 1) {
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                finger_distance = getFingerSpacing(event);
                Log.d(TAG, "motion event action_pointer_down distance: " + finger_distance);
            } else if (action == MotionEvent.ACTION_MOVE && parameters.isZoomSupported()) {
                Log.d(TAG, "motion event action_move");
                camera.cancelAutoFocus();
                finger_distance = handleZoomGesture(event, finger_distance);
            }
        }
        return true;
    }

    //calculate the distance between two fingers
    private double getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }



}


