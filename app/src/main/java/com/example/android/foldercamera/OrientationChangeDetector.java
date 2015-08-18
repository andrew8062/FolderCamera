package com.example.android.foldercamera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Andrew on 8/17/2015.
 */
public class OrientationChangeDetector {
    private static final String TAG = "fc:orientationChange";
    private Context mContext;
    private Handler mHandler;
    int orientation = -1;
    SensorManager sensorManager;
    public OrientationChangeDetector(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values[1] < 6.5 && event.values[1] > -6.5) {
                if(orientation != 1 && event.values[0] > 0) {
                    Log.d(TAG, "Landscape head to left");
                    mHandler.obtainMessage(MainActivity.ORIENTAION_CHANGE, 1,0).sendToTarget();
                    orientation = 1;
                }else if(orientation != 2 && event.values[0] < 0) {
                    Log.d(TAG, "Landscape head to right");
                    mHandler.obtainMessage(MainActivity.ORIENTAION_CHANGE, 2,0).sendToTarget();
                    orientation = 2;
                }
            } else {
                if (orientation != 0) {
                    Log.d(TAG, "Portrait");
                    mHandler.obtainMessage(MainActivity.ORIENTAION_CHANGE, 0,0).sendToTarget();
                    orientation = 0;
                }

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}
