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
    int orientation = -1;
    SensorManager sensorManager;
    private Context mContext;
    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.values[1] < 6.5 && event.values[1] > -6.5) {
                if (orientation != 1 && event.values[0] > 0) {
                    Log.d(TAG, "Landscape head to left");
                    orientation = 1;
                } else if (orientation != 2 && event.values[0] < 0) {
                    Log.d(TAG, "Landscape head to right");
                    orientation = 2;
                }
            } else {
                if (orientation != 0) {
                    Log.d(TAG, "Portrait");
                    orientation = 0;
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
