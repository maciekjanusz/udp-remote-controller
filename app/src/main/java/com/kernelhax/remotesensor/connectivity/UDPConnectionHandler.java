package com.kernelhax.remotesensor.connectivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.kernelhax.remotesensor.connectivity.UDPClient.*;

public class UDPConnectionHandler extends Handler {

    private static final String TAG = "UDPConn";

    private static final int SOCKET_TIMEOUT_MILLIS = 100;

    private DatagramSocket sendSocket;
    private DatagramSocket receiveSocket;

    private Thread listenThread = new Thread(new Runnable() {

        byte[] recData = new byte[1024];

        @Override
        public void run() {

            DatagramPacket recPacket =
                    new DatagramPacket(recData, recData.length);

            while(connected) {
                try {
                    Log.i(TAG, " --- Waiting for a packet...");
                    receiveSocket.receive(recPacket);
                    String recMessage = new String(recPacket.getData());
//                        Log.i(TAG, "RECEIVED: " + recMessage);

                } catch (IOException e) {
//                        e.printStackTrace();
                }
            }
        }
    });

    private volatile boolean connected = false;

    public UDPConnectionHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case CONNECT:
                Log.e(TAG, "connecting...");
                Bundle data = msg.getData();
                byte[] ipAddress = data.getByteArray(DATA_IP);
                int sendPort = data.getInt(DATA_SEND_PORT);
                int receivePort = data.getInt(DATA_RECEIVE_PORT);
                // init sendSocket
                connect(ipAddress, sendPort, receivePort);
                break;

            case DISCONNECT:
                Log.e(TAG, "disconnecting...");
                disconnect();
                break;

            case SEND:
//                    Log.i(TAG, "sending...");
                DatagramPacket packet = (DatagramPacket) msg.obj;
                sendData(packet);
                break;
        }
    }

    private void sendData(DatagramPacket packet) {
        if(connected) {
            try {
//                Log.i(TAG, "sending packet");
                sendSocket.send(packet);
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

    private void connect(byte[] ipAddress, int sendPort, int receivePort) {
        try {
            InetAddress serverAddress = InetAddress.getByAddress(ipAddress);

            sendSocket = new DatagramSocket();
            sendSocket.connect(serverAddress, sendPort);

//            receiveSocket = new DatagramSocket();
//            receiveSocket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
//            receiveSocket.connect(serverAddress, receivePort);

            connected = true;
//            listen();

        } catch (SocketException | UnknownHostException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private void disconnect() {
        if(sendSocket != null) {
            connected = false;

//            if(listenThread.isAlive()) {
//                try {
//                    listenThread.join();
//                } catch (InterruptedException e) {
//                    //e.printStackTrace();
//                    Log.e(TAG, "Join interrupted");
//                }
//            }

            sendSocket.disconnect();
            sendSocket.close();

//            receiveSocket.disconnect();
//            receiveSocket.close();
        }
    }

    private void listen() {
        if(!listenThread.isAlive()) {
            listenThread.start();
        }
    }
}
