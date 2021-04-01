package ca.bcit.udpchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private static final String hostString =  "192.168.1.76";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickSendMessage(View v) {
        final String message = "Watson, come here, I need you";

        Data d = new Data(message);
        sendMessage(d);
    }

    public void onClickSendBattle(View v) {
        Data d = new Data(R.raw.battle);

        sendMessage(d);
    }

    public void sendMessage(final Data data) {
        //So, this needs to set up a UPD socket and then send a datagram.
        //Wait... that's it?
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    InetAddress address = InetAddress.getByName(hostString);

                    byte[] messageB;

                    messageB = data.getMessage().getBytes(); //Add code for a song

                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(messageB, messageB.length, address, 65432);
                    socket.send(packet);
                    socket.disconnect();
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

    public void call(View v) {

    }

    public void waitForCall(View v) {

    }
}