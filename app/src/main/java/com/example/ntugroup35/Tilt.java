package com.example.ntugroup35;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Class for tilting the screen
 */

public class Tilt {
    public interface Listener{
        void onRotation(float rx, float ry, float rz);
    }

    /**
     * Listener to listen for change of gyroscope
     */
    private Listener listener;

    /**
     * Set current listener
     * @param l listener
     */
    public void setListener(Listener l){
        listener = l;
    }

    /**
     * Sensor Manager for tilt
     */
    private SensorManager sensorManager;

    /**
     * Sensor
     */
    private Sensor sensor;

    /**
     * Event Listener for sensor
     */
    private SensorEventListener sensorEventListener;

    /**
     * Constructor of Tilt class
     *
     * @param context activity context
     */
    Tilt(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorEventListener = new SensorEventListener() {
            /**
             * Override class for sensor event listener
             *
             * @param sensorEvent
             */
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(listener!=null){
                    listener.onRotation(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
                }
            }

            /**
             * Override class
             *
             * @param sensor sensor for gyroscope
             * @param i
             */
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    /**
     * Register listener to listen for change on sensor
     */
    public void register(){
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * De-register listener to listen for change on sensor
     *
     */
    public void unregister(){
        sensorManager.unregisterListener(sensorEventListener);
    }
}
