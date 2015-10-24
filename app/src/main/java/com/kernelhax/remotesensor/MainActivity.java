package com.kernelhax.remotesensor;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.kernelhax.remotesensor.connectivity.UDPClient;
import com.kernelhax.remotesensor.input.IPInputDialogFragment;
import com.kernelhax.remotesensor.sensor.Compass;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        Compass.CompassListener, IPInputDialogFragment.Callbacks {

    private static final char SEPARATOR = ':';
    private static final int PORT = 8000;
    private static final String IP_INPUT_FRAGMENT_TAG = "ip_input_dialog";

    private UDPClient udpClient = new UDPClient();
    private Compass compass;
    private final DialogFragment ipInputDialogFragment = new IPInputDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        compass = new Compass(this, this);
    }

    @OnClick(R.id.send_button)
    public void onSendButtonClick() {
//        String message = "kurwachuj";
//        udpClient.send(message.getBytes());

        if(!ipInputDialogFragment.isAdded()) {
            ipInputDialogFragment.show(getSupportFragmentManager(), IP_INPUT_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onDestroy() {
        udpClient.disconnect();
        compass.stop();
        super.onDestroy();
    }

    @Override
    public void onCompassStateChanged(float bearing, float pitch, float roll) {
        int b = (int) bearing;
        int p = (int) pitch;
        int r = (int) roll;

        String message = new StringBuilder()
                .append(String.valueOf(b)).append(SEPARATOR)
                .append(p).append(SEPARATOR).append(r)
                .toString();
        udpClient.send(message.getBytes());
    }

    @Override
    public void onIpEntered(byte[] ip) {
        udpClient.connect(ip, PORT);
//            compass.start();
    }
}
