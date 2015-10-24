package com.kernelhax.remotesensor.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.kernelhax.remotesensor.R;
import com.kernelhax.remotesensor.connectivity.UDPClient;
import com.kernelhax.remotesensor.sensor.Compass;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ControllerFragment extends Fragment implements Compass.CompassListener {

    private static final String TAG = "ControllerFragment";
    private static final String DATA_IP_ADDRESS = "ip_address";
    private static final String DATA_BEARING_MODIFIER = "bearing_modifier";

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;

    private static final char SEPARATOR = ':';
    private static final int PORT = 8000;
    private static final long INITIAL_DELAY = 1000; // millis
    private static final long DELAY = 10; // millis

    @Bind(R.id.thrust_slider)  ThrustSlider thrustSlider;
    @Bind(R.id.missile_button) Button missileButton;
    @Bind(R.id.laser_button)   Button laserButton;

    private UDPClient udpClient = new UDPClient();
    private Compass compass;
    private Runnable transmitControlsRunnable = new TransmitControlsRunnable();

    private int bearing;
    private int pitch;
    private int roll;
    private byte[] ipAddress;
    private float bearingModifier;

    public static ControllerFragment newInstance(byte[] ipAddress, float bearingModifier) {
        Bundle args = new Bundle();
        args.putByteArray(DATA_IP_ADDRESS, ipAddress);
        args.putFloat(DATA_BEARING_MODIFIER, bearingModifier);
        ControllerFragment fragment = new ControllerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getArguments();
        ipAddress = data.getByteArray(DATA_IP_ADDRESS);
        bearingModifier = data.getFloat(DATA_BEARING_MODIFIER);

        compass = new Compass(getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_controller, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void startControlsTransmission() {
        scheduledFuture = executorService.scheduleWithFixedDelay(transmitControlsRunnable,
                INITIAL_DELAY, DELAY, TimeUnit.MILLISECONDS);
    }

    private void stopControlsTransmission() {
        if(scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
    }

    class TransmitControlsRunnable implements Runnable {

        @Override
        public void run() {
            int missile = missileButton.isPressed() ? 1 : 0;
            int laser = laserButton.isPressed() ? 1 : 0;
            int thrust = (int) (thrustSlider.getThrustLevel() * 100);

            String controlsMessage = String.valueOf(bearing)
                    + SEPARATOR + pitch
                    + SEPARATOR + roll
                    + SEPARATOR + thrust
                    + SEPARATOR + missile
                    + SEPARATOR + laser;

//            Log.i(TAG, controlsMessage);

            udpClient.send(controlsMessage.getBytes());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // keep screen on
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        udpClient.connect(ipAddress, PORT);
        compass.start();
        startControlsTransmission();
    }

    @Override
    public void onPause() {
        stopControlsTransmission();
        compass.stop();
        udpClient.disconnect();

        // release keep screen on
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onDestroy();
    }

    @Override
    public void onCompassStateChanged(float b, float p, float r) {
//        this.bearing = ((int)(b - bearingModifier) % 360);
//        this.bearing = (int) b;
        this.bearing = (int) ((b - bearingModifier + 360f) % 360f);
        this.pitch = (int) p;
        this.roll = (int) r;
    }
}
