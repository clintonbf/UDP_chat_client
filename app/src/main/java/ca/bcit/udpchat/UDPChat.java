package ca.bcit.udpchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPChat extends AppCompatActivity {
    private static final String hostString =  "192.168.1.76";
    private Button connect;
    private Button chat;
    private Button hang_up;
    DatagramSocket socket;

    private static final int NOT_CONNECTED = 1;
    private static final int IS_CONNECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_d_p_chat);

        connect = (Button) findViewById(R.id.connect_btn);
        chat = (Button) findViewById(R.id.chat_btn);
        hang_up = (Button) findViewById(R.id.hangup_btn);
        enableButtonsByFunction(NOT_CONNECTED);


        hang_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socket != null) {
                    socket.close();
                    socket.disconnect();
                    socket = null;

                    enableButtonsByFunction(NOT_CONNECTED);
                }

                Log.i("UDP Connection", "Disconnected from server");
            }
        });

        hang_up.setEnabled(false);
    }

    public void connectToUDPServer(View v) {
        final String message = "Connect";

        final Data data = new Data(message);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress address = InetAddress.getByName(hostString);

                    byte[] messageB = data.getMessage().getBytes(); //Add code for a song

                    socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(messageB, messageB.length, address, Environment.PORT);
                    socket.send(packet);

                    Log.i("UDP handshake", "Connected to UDP server");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        enableButtonsByFunction(IS_CONNECTED);
    }

    public void startTalking(View v) {
        //start the audio recorder
        //stream packets to the server

    }

    private void enableButtonsByFunction(final int function) {
        switch (function) {
            case NOT_CONNECTED:
                connect.setEnabled(true);
                hang_up.setEnabled(false);
                chat.setEnabled(false);
                break;
            case IS_CONNECTED:
                connect.setEnabled(false);
                hang_up.setEnabled(true);
                chat.setEnabled(true);
                break;
            default:
                break;
        }
    }
}