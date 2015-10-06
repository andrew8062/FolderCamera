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
    private final double ORIENTATION_THRESHOLD = 6.5;
    public static final int HEAD_TO_LEFT = 1;
    public static final int HEAD_PORTRIAT = 0;
    public static final int HEAD_TO_RIGHT = 2;
    int orientation = -1;
    SensorManager sensorManager;
    private Context mContext;
    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values[1] < ORIENTATION_THRESHOLD && event.values[1] > -ORIENTATION_THRESHOLD) {
                if (orientation != 1 && event.values[0] > 0) {
                    Log.d(TAG, "Landscape head to left");
                    orientation = HEAD_TO_LEFT;
                } else if (orientation != 2 && event.values[0] < 0) {
                    Log.d(TAG, "Landscape head to right");
                    orientation = HEAD_TO_RIGHT;
                }
            } else {
                if (orientation != 0) {
                    Log.d(TAG, "Portrait");
                    orientation = HEAD_PORTRIAT;
                }

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public OrientationChangeDetector(Context context) {
        mContext = context;
        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void register() {
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister() {
        sensorManager.unregisterListener(sensorEventListener);
    }
    public int getOrientation(){
        return orientation;
    }
}
