package ca.bcit.udpchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPChat extends AppCompatActivity {
    private static final String hostString =  "192.168.1.76";
    private Button connect;
    private Button chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_d_p_chat);

        connect = (Button) findViewById(R.id.connect_btn);
        chat = (Button) findViewById(R.id.chat_btn);

        chat.setEnabled(false);
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

                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(messageB, messageB.length, address, Environment.PORT);
                    socket.send(packet);

                    Log.i("UDP handshake", "Connected to UDP server");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        connect.setEnabled(false);
        chat.setEnabled(true);
    }

    public void startTalking(View v) {

    }
}