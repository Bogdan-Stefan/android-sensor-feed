package com.example.bogdan_stefan.sensorfeed;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        statusCard = (CardView) findViewById(R.id.status_card);
        statusText = (TextView) findViewById(R.id.status_text);
        xAxisText = (TextView) findViewById(R.id.x_axis_text);
        yAxisText = (TextView) findViewById(R.id.y_axis_text);
        zAxisText = (TextView) findViewById(R.id.z_axis_text);

        try {
            serverAddress = new URI("ws://localhost:port");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            exceptionRaised("Invalid server address!");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorFeedClient = new SensorFeedClient(serverAddress, this);
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
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xAxisText.setText(Float.toString(event.values[0]));
        yAxisText.setText(Float.toString(event.values[1]));
        zAxisText.setText(Float.toString(event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.out.println("Accuracy changed!");
    }

    @Override
    public void connectionOpened() {
        int colorID = ResourcesCompat.getColor(getResources(), R.color.status_ok, null);
        changeStatusCard(colorID, "Connected");
    }

    @Override
    public void connectionClosed() {
        int colorID = ResourcesCompat.getColor(getResources(), R.color.status_default, null);
        changeStatusCard(colorID, "Disconnected");
    }

    @Override
    public void exceptionRaised(String errorMessage) {
        int colorID = ResourcesCompat.getColor(getResources(), R.color.status_error, null);
        changeStatusCard(colorID, errorMessage);
    }

    private void changeStatusCard(int colorID, String message) {
        statusCard.setCardBackgroundColor(colorID);
        statusText.setText(message);
    }
}
