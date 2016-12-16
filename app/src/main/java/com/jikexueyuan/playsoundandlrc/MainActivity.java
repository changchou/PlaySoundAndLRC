package com.jikexueyuan.playsoundandlrc;

import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private TextView tvLRCOut;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLRCOut = (TextView) findViewById(R.id.tvLrcOut);

        mediaPlayer = MediaPlayer.create(this, R.raw.music);

        try {
            mediaPlayer.addTimedTextSource(getSrtFile(R.raw.lrc), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
            int textTrackIndex = findTrackIndex(MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mediaPlayer.getTrackInfo());
            if (textTrackIndex >= 0) {
                mediaPlayer.selectTrack(textTrackIndex);
            }
            mediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
                @Override
                public void onTimedText(final MediaPlayer mp, final TimedText text) {
                    if (text != null) {
                        handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvLRCOut.setText(text.getText());
                            }
                        });
                    }
                }
            });
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private int findTrackIndex(int mediaTrackType, MediaPlayer.TrackInfo[] trackInfos) {
        int index = -1;
        for (int i = 0; i < trackInfos.length; i++) {
            if (trackInfos[i].getTrackType() == mediaTrackType) {
                return i;
            }
        }
        return index;
    }

    private String getSrtFile(int resId) {
        String fileName = getResources().getResourceEntryName(resId);
        File srtFile = getFileStreamPath(fileName);
        if (srtFile.exists()) {
            return srtFile.getAbsolutePath();

        }

        InputStream ips = null;
        OutputStream ops = null;

        try {
            ips = getResources().openRawResource(resId);
            ops = new FileOutputStream(srtFile, false);
            byte[] buf = new byte[1024];
            int l;
            while ((l = ips.read(buf)) != -1) {
                ops.write(buf, 0, l);
            }

            return srtFile.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ops != null) {
                try {
                    ops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ips != null) {
                try {
                    ips.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";

    }

}
