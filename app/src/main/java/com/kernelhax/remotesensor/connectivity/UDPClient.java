package com.kernelhax.remotesensor.connectivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {

    public static final int CONNECT = 0;
    public static final int DISCONNECT = 1;
    public static final int SEND = 2;

    public static final String DATA_IP = "ip";
    public static final String DATA_PORT = "port";

    private static final String TAG = "UDPClient";
    private Handler udpHandler;

    public UDPClient() {
        HandlerThread thread = new HandlerThread("udp");
        thread.start();
        udpHandler = new UdpHandler(thread.getLooper());
    }

    public void send(byte[] sendData) {
        Log.i(TAG, "send command");
        // todo: maybe try recycling
        DatagramPacket packet = new DatagramPacket(
                sendData, sendData.length);

        Message msg = udpHandler.obtainMessage(SEND);
        msg.obj = packet;

        udpHandler.sendMessage(msg);
    }

    public void connect(byte[] ipAddress, int port) {
        Bundle data = new Bundle();
        data.putByteArray(DATA_IP, ipAddress);
        data.putInt(DATA_PORT, port);
        Message msg = udpHandler.obtainMessage(CONNECT);
        msg.setData(data);

        udpHandler.sendMessage(msg);
    }

    public void disconnect() {
        udpHandler.sendEmptyMessage(DISCONNECT);
    }

    static class UdpHandler extends Handler {

        private DatagramSocket socket;
        private boolean connected = false;

        public UdpHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECT:
                    Log.i(TAG, "connecting...");
                    Bundle data = msg.getData();
                    byte[] ipAddress = data.getByteArray(DATA_IP);
                    int port = data.getInt(DATA_PORT);
                    // init socket
                    connect(ipAddress, port);
                    break;

                case DISCONNECT:
                    Log.i(TAG, "disconnecting...");
                    disconnect();
                    break;

                case SEND:
                    Log.i(TAG, "sending...");
                    DatagramPacket packet = (DatagramPacket) msg.obj;
                    sendData(packet);
                    break;
            }
        }

        private void sendData(DatagramPacket packet) {
            if(connected) {
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }

        private void connect(byte[] ipAddress, int port) {
            try {
                socket = new DatagramSocket();
                InetAddress serverAddress = InetAddress.getByAddress(ipAddress);
                socket.connect(serverAddress, port);
                connected = true;
            } catch (SocketException | UnknownHostException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        private void disconnect() {
            if(socket != null) {
                connected = false;
                socket.disconnect();
                socket.close();
            }
        }
    }
}
