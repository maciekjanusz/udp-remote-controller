package com.kernelhax.remotesensor.connectivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.net.DatagramPacket;

public class UDPClient {

    public static final int CONNECT = 0;
    public static final int DISCONNECT = 1;
    public static final int SEND = 2;

    public static final String DATA_IP = "ip";
    public static final String DATA_SEND_PORT = "send_port";
    public static final String DATA_RECEIVE_PORT = "receiver_port";

    private static final int DEFAULT_SEND_PORT = 8001;
    private static final int DEFAULT_RECEIVE_PORT = 8002;

    private static final String TAG = "UDPClient";
    private Handler udpHandler;

    public UDPClient() {
        HandlerThread thread = new HandlerThread("udp");
        thread.start();
        udpHandler = new UDPConnectionHandler(thread.getLooper());
    }

    public void send(byte[] sendData) {
//        Log.i(TAG, "send command");
        // todo: maybe try recycling
        DatagramPacket packet = new DatagramPacket(
                sendData, sendData.length);

        Message msg = udpHandler.obtainMessage(SEND);
        msg.obj = packet;

        udpHandler.sendMessage(msg);
    }

    public void connect(byte[] ipAddress) {
        connect(ipAddress, DEFAULT_SEND_PORT, DEFAULT_RECEIVE_PORT);
    }

    public void connect(byte[] ipAddress, int sendPort, int receivePort) {
        Bundle data = new Bundle();
        data.putByteArray(DATA_IP, ipAddress);
        data.putInt(DATA_SEND_PORT, sendPort);
        data.putInt(DATA_RECEIVE_PORT, receivePort);
        Message msg = udpHandler.obtainMessage(CONNECT);
        msg.setData(data);

        udpHandler.sendMessage(msg);
    }

    public void disconnect() {
        udpHandler.sendEmptyMessage(DISCONNECT);
    }
}