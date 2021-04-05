package ca.bcit.udpchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.Arrays;

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

    public void goToPlayer(View v) {
        Intent i = new Intent(this.getApplicationContext(), Player.class);
        startActivity(i);
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

    public void sendLongMessage(final int resource) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final int chunkSize = 512;
                final int PORT = 65432;
                byte[] messageB = new byte[0];

//                final String longMessage = new BigAssText().getText();
//                messageB = longMessage.getBytes();

                try {
                    messageB = readInSound(resource); // <----- reading in a sound file
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    InetAddress address = InetAddress.getByName(hostString);

                    DatagramSocket socket = new DatagramSocket();

                    byte[][] subPackets = splitBytes(messageB, chunkSize);

                    //Send song length
                    byte[] songLength = BigInteger.valueOf(subPackets.length).toByteArray();
                    System.out.println("Transferring " + subPackets.length + " chunks");

                    DatagramPacket size = new DatagramPacket(songLength, songLength.length, address, PORT);
                    socket.send(size);

                    for (byte[] subPacket : subPackets) {
                        DatagramPacket packet = new DatagramPacket(subPacket,
                                subPacket.length,
                                address,
                                PORT);
                        socket.send(packet);
                    }

                    socket.disconnect();
                    socket.close();

                    System.out.println("File transferred");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
    }

    public void sendBattle(View v) {
        this.sendLongMessage(R.raw.battle);
    }

    public void sendNatural(View v) {
        this.sendLongMessage(R.raw.song);
    }

    public void sendMeep(View v) {
        this.sendLongMessage(R.raw.meepmeep);
    }

    public byte[] readInSound(final int resource) throws Exception {
        InputStream in = getApplicationContext().getResources().openRawResource(resource);

        byte[] music = new byte[in.available()];
        int read = in.read(music);
        System.out.println("Read in " + read + " bytes");
        return music;
    }

    public void call(View v) {

    }

    public void waitForCall(View v) {

    }

    public byte[][] splitBytes(final byte[] data, final int chunkSize) {
        final int length = data.length;
        final byte[][] dest = new byte[(length + chunkSize - 1)/chunkSize][];
        int destIndex = 0;
        int stopIndex = 0;

        for (int startIndex = 0; startIndex + chunkSize <= length; startIndex += chunkSize)
        {
            stopIndex += chunkSize;
            dest[destIndex++] = Arrays.copyOfRange(data, startIndex, stopIndex);
        }

        if (stopIndex < length)
            dest[destIndex] = Arrays.copyOfRange(data, stopIndex, length);

        return dest;
    }
}