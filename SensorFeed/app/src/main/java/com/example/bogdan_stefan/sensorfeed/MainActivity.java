package com.example.bogdan_stefan.sensorfeed;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity
        implements WebSocketEventHandler, SensorEventListener {

    private URI serverAddress;
    private SensorFeedClient sensorFeedClient;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private CardView statusCard;
    private TextView statusText;
    private TextView xAxisText;
    private TextView yAxisText;
    private TextView zAxisText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorFeedClient = null;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        statusCard = (CardView) findViewById(R.id.status_card);
        statusText = (TextView) findViewById(R.id.status_text);
        xAxisText = (TextView) findViewById(R.id.x_axis_text);
        yAxisText = (TextView) findViewById(R.id.y_axis_text);
        zAxisText = (TextView) findViewById(R.id.z_axis_text);

        try {
            serverAddress = new URI("ws://your-server:9000/ws");
        } catch (URISyntaxException e) {
            exceptionRaised("Invalid server address!", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorFeedClient = new SensorFeedClient(serverAddress, this);
        sensorFeedClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorFeedClient.close();
        sensorFeedClient = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String xReading = Float.toString(event.values[0]);
        String yReading = Float.toString(event.values[1]);
        String zReading = Float.toString(event.values[2]);

        xAxisText.setText(xReading);
        yAxisText.setText(yReading);
        zAxisText.setText(zReading);

        if (sensorFeedClient != null && sensorFeedClient.getReadyState() == 1) {
            String payload = Build.MODEL + "," + xReading + "," + yReading + "," + zReading;
            sensorFeedClient.send(payload);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.out.println("Accuracy changed!");
    }

    @Override
    public void connectionOpened() {
        int colorID = ResourcesCompat.getColor(getResources(), R.color.status_ok, null);
        runOnUiThread(new StatusChanger(colorID, "Connected"));
    }

    @Override
    public void connectionClosed() {
        int colorID = ResourcesCompat.getColor(getResources(), R.color.status_default, null);
        runOnUiThread(new StatusChanger(colorID, "Disconnected"));
    }

    @Override
    public void exceptionRaised(String errorMessage, Exception e) {
        int colorID = ResourcesCompat.getColor(getResources(), R.color.status_error, null);
        runOnUiThread(new StatusChanger(colorID, errorMessage));
        e.printStackTrace();
    }

    private class StatusChanger implements Runnable {
        private int colorID;
        private String message;

        StatusChanger(int colorID, String message) {
            this.colorID = colorID;
            this.message = message;
        }

        @Override
        public void run() {
            statusCard.setCardBackgroundColor(colorID);
            statusText.setText(message);
        }
    }
}
