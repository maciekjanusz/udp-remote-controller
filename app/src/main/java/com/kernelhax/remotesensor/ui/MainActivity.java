package com.kernelhax.remotesensor.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.kernelhax.remotesensor.R;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        SetupFragment.OnSetupFinishedListener {

    private static final String TAG = "MainActivity";

    private static final String IP_INPUT_FRAGMENT_TAG = "ip_input_dialog";
    private static final String CONTROLER_FRAGMENT_TAG = "controller_fragment";

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fm = getSupportFragmentManager();

        Fragment setupFragment = fm.findFragmentByTag(CONTROLER_FRAGMENT_TAG);
        if(setupFragment == null) {
            setupFragment = new SetupFragment();
        }

        fm.beginTransaction().add(R.id.fragment_container, setupFragment).commit();
    }

//    @OnClick(R.id.send_button)
//    public void onSendButtonClick() {
////        String message = "kurwachuj";
////        udpClient.send(message.getBytes());
//
//        if(!ipInputDialogFragment.isAdded()) {
//            ipInputDialogFragment.show(getSupportFragmentManager(), IP_INPUT_FRAGMENT_TAG);
//        }
//    }


//    @Override
//    public void onIpEntered(byte[] ip) {
////        udpClient.connect(ip, PORT);
//        //            compass.start();
////        startControlsTransmission();
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    private void enableImmersiveMode() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    @Override
    public void onSetupFinished(byte[] ipAddress, float bearingModifier) {
        ControllerFragment controllerFragment = ControllerFragment.newInstance(ipAddress, bearingModifier);
        fm.beginTransaction().replace(R.id.fragment_container, controllerFragment).commit();
        enableImmersiveMode();
        Log.i(TAG, "Bearing: " + bearingModifier);
    }
}
