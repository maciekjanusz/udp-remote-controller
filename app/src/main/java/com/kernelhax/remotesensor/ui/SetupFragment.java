package com.kernelhax.remotesensor.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kernelhax.remotesensor.R;
import com.kernelhax.remotesensor.input.IPInputView;
import com.kernelhax.remotesensor.sensor.Compass;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetupFragment extends Fragment implements Compass.CompassListener {

    private OnSetupFinishedListener callback;
    private Compass compass;

    @Bind(R.id.ip_input_view)
    IPInputView ipInputView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnSetupFinishedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement SetupFragment.Callbacks");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compass = new Compass(getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setup, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.start_button)
    public void onStartButtonClick() {
        compass.start();
    }

    @Override
    public void onCompassStateChanged(float bearing, float pitch, float roll) {
        compass.stop();
        byte[] ip = ipInputView.getIpAddress();

        callback.onSetupFinished(ip, bearing);
    }

    public interface OnSetupFinishedListener {
        void onSetupFinished(byte[] ipAddress, float bearingModifier);
    }
}
