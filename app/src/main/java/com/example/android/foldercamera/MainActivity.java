package com.example.android.foldercamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends Activity implements SurfaceHolder.Callback {
    private static final String TAG = "fc";
    private Camera camera;
    private Camera.Parameters parameters;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Button button1;
    ImageView imageView1;
    FloatingActionMenu fam;
    FloatingActionButton fab;
    private GestureDetector gestureDetector;
    int currentZoomLevel;
    double finger_distance = 0;
    private Button folderFloatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.float_button).setOnClickListener(famOnClickListener);
        //gestureDetector = new GestureDetector(this);



        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        button1 = (Button)

                findViewById(R.id.button1);

        //在AndroidManifest.xml中設定或是用下面的setRequestedOrientation(0)設定也可以
        //0代表橫向、1代表縱向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //設為横向顯示。因為攝影頭會自動翻轉90度，所以如果不横向顯示，看到的畫面就是翻轉的。

        surfaceView = (SurfaceView)

                findViewById(R.id.surfaceView);

        imageView1 = (ImageView)

                findViewById(R.id.imageView1);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
        button1.setOnClickListener(new View.OnClickListener()

                                   {

                                       public void onClick(View v) {

                                           //自動對焦
                                           camera.autoFocus(afcb);
                                       }
                                   }

        );

    }

    private void setupZoomControl() {
        ZoomControls zoomControls = (ZoomControls) findViewById(R.id.zoomControls);
        if (parameters.isZoomSupported()) {
            final int maxZoomLevel = parameters.getMaxZoom();
            currentZoomLevel = 0;
            zoomControls.setIsZoomInEnabled(true);
            zoomControls.setIsZoomOutEnabled(true);
            Log.d(TAG, "smoothZoom:" + parameters.isSmoothZoomSupported());
            Log.d(TAG, "maxZoom:" + maxZoomLevel);
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

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    public void surfaceCreated(SurfaceHolder holder) {

        camera = Camera.open();
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
        if (event.getPointerCount() > 1) {
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                finger_distance = getFingerSpacing(event);
                Log.d(TAG, "motion event action_pointer_down distance: " + finger_distance);
            } else if (action == MotionEvent.ACTION_MOVE && parameters.isZoomSupported()) {
                Log.d(TAG, "motion event action_move");
                camera.cancelAutoFocus();
                finger_distance = handleZoom(event, finger_distance);
            }
        } else {

            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event);
            }

        }
        return true;
        //return gestureDetector.onTouchEvent(event);
    }

//    private void handleFocus(MotionEvent event) {
//        int pointerId = event.getPointerId(0);
//        int pointerIndex = event.findPointerIndex(pointerId);
//
//        float x = event.getX(pointerIndex);
//        float y = event.getY(pointerIndex);
//
//        Rect focusRect = calculateFocusArea(x, y);
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//
//        Camera.Area focusArea = new Camera.Area(focusRect, 500);
//        List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
//        focusAreas.add(focusArea);
//        parameters.setFocusAreas(focusAreas);
//        camera.setParameters(parameters);





//        List<String> supportedFocusMode = parameters.getSupportedFocusModes();
//        if (supportedFocusMode != null && supportedFocusMode.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//            camera.autoFocus(new Camera.AutoFocusCallback() {
//                @Override
//                public void onAutoFocus(boolean success, Camera camera) {
//                    camera.cancelAutoFocus();
//                }
//            });
//        }

    }

//    private Rect calculateFocusArea(float x, float y){
//        Rect touchRect = new Rect((int) (x - 100),
//                (int) (y - 100),
//                (int) (x + 100),
//                (int) (y + 100));
//
//        Rect targetFocusRect = new Rect(
//
//                touchRect.left * 2000/surfaceView.getWidth() - 1000,
//                touchRect.top * 2000/surfaceView.getHeight() - 1000,
//                touchRect.right * 2000/surfaceView.getWidth() - 1000,
//                touchRect.bottom * 2000/surfaceView.getHeight() - 1000);
//        return targetFocusRect;
//    }

    private double getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    private double handleZoom(MotionEvent event, double mDist) {
        double newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            zoomCamera(true);
        } else if (newDist < mDist) {
            zoomCamera(false);
        }
        return newDist;
    }

    PictureCallback jpeg = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            //byte數组轉換成Bitmap
            imageView1.setImageBitmap(bmp);
            //拍下圖片顯示在下面的ImageView裡
            FileOutputStream fop;
            try {
                fop = new FileOutputStream("/sdcard/dd.jpg");
                //實例化FileOutputStream，參數是生成路徑
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fop);
                //壓缩bitmap寫進outputStream 參數：輸出格式  輸出質量  目標OutputStream
                //格式可以為jpg,png,jpg不能存儲透明
                fop.close();
                System.out.println("拍照成功");
                //關閉流
            } catch (FileNotFoundException e) {

                e.printStackTrace();
                System.out.println("FileNotFoundException");

            } catch (IOException e) {

                e.printStackTrace();
                System.out.println("IOException");
            }
            camera.startPreview();
            //需要手動重新startPreview，否則停在拍下的瞬間
        }

    };


    //自動對焦監聽式
    Camera.AutoFocusCallback afcb = new Camera.AutoFocusCallback() {

        public void onAutoFocus(boolean success, Camera camera) {

            if (success) {
                //對焦成功才拍照
                camera.takePicture(null, null, jpeg);

            }
        }
    };

    private View.OnClickListener famOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "famisclicked");
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setTitle("select folder");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("Hardik");
            arrayAdapter.add("Archit");
            arrayAdapter.add("Jignesh");
            arrayAdapter.add("Umang");
            arrayAdapter.add("Gatti");

            dialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialogBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String folderName = arrayAdapter.getItem(which);
                    Toast.makeText(MainActivity.this, "you select " + folderName, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "you select " + folderName);
                }
            });
            dialogBuilder.show();
        }
    };

//    @Override
//    public boolean onDown(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent e) {
//
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//        return false;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent e) {
//
//    }
//
//    private static final int SWIPE_MIN_DISTANCE = 120;
//    private static final int SWIPE_MAX_OFF_PATH = 250;
//    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        Log.d(TAG, "onFling");
//        Log.d(TAG, "Flags: " + e1.getEdgeFlags());
//
//        if (e1.getEdgeFlags() == MotionEvent.EDGE_LEFT) {
//            // code to handle swipe from left edge
//            Log.d(TAG, "Edge fling!");
//        }
//
//        try {
//            float distance = e1.getX() - e2.getX();
//
//            Log.d(TAG, "fling distance: "+distance+"velocity: "+velocityX);
//            // do not do anything if the swipe does not reach a certain length
//            // of distance
//            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
//                return false;
//
//            // right to left swipe
//            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
//                    && Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
//                Log.d(TAG, "right to left!");
//
//
//            }
//            // left to right swipe
//            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
//                    && Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
//                Log.d(TAG, "left to right!");
//
//            }
//        } catch (Exception e) {
//            // nothing
//        }
//        return false;
//    }
}