package ca.bcit.udpchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SendAVoiceMemo extends AppCompatActivity {
    private static final String SERVER  = "192.168.1.76";
    private static final int PORT       = 65432;
    private boolean mic                 = false;
    private InetAddress address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_a_voice_memo);
    }

    public void sendAMemo(View v) {
        final int       SAMPLE_RATE     = 10000;
        final int       CHANNELS        = 1; //mono
        final int       BIT_DEPTH       = 16; //bytes, I think
        final double    TIME_FRAME      = 0.5; //seconds
        final int       BUFFER_FACTOR   = 10;
        final int       SAMPLE_SIZE     = 2;
        final int       SAMPLE_INTERVAL = 20;
        final int       BUF_SIZE        = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2;

        this.checkRecordPermission();

        mic = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AudioRecord audioRecorder = new AudioRecord(
                        MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        AudioRecord.getMinBufferSize(
                                SAMPLE_RATE,
                                AudioFormat.CHANNEL_IN_MONO,
                                AudioFormat.ENCODING_PCM_16BIT) * BUFFER_FACTOR);


                int bytesRead = 0;
                byte[] buffer= new byte[BUF_SIZE];
                try {
                    address = InetAddress.getByName(SERVER);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                try {
                    DatagramSocket socket = new DatagramSocket();

                    audioRecorder.startRecording();

                    while (mic) {
                        bytesRead = audioRecorder.read(buffer, 0, BUF_SIZE);

                        byte[] songLength = BigInteger.valueOf(bytesRead).toByteArray();
                        DatagramPacket size = new DatagramPacket(songLength, songLength.length, address, PORT);
                        socket.send(size);

                        DatagramPacket packet = new DatagramPacket(buffer, bytesRead, address, PORT);
                        socket.send(packet);
                        Thread.sleep(SAMPLE_INTERVAL, 0);
                    }

                    audioRecorder.stop();
                    audioRecorder.release();
                    socket.disconnect();
                    socket.close();
                    mic = false;
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void checkRecordPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    123);
        }
    }

    public void toggleMic(View v) {
        this.mic = ! this.mic;
    }

    public void turnMicOff(View v) {
        this.mic = false;
    }
}