package com.example.nhatnguyen.caller;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AudioPlayer {

    static final String LOG_TAG = AudioPlayer.class.getSimpleName();

    private Context mContext;

    private MediaPlayer mPlayer;

    private AudioTrack mProgressTone;

    private final static int SAMPLE_RATE = 16000;

    public AudioPlayer(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void playRingtone() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        // Honour silent mode
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {

                }

                try {
                    Class<?> cMediaTimeProvider = Class.forName( "android.media.MediaTimeProvider" );
                    Class<?> cSubtitleController = Class.forName( "android.media.SubtitleController" );
                    Class<?> iSubtitleControllerAnchor = Class.forName( "android.media.SubtitleController$Anchor" );
                    Class<?> iSubtitleControllerListener = Class.forName( "android.media.SubtitleController$Listener" );

                    Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});

                    Object subtitleInstance = constructor.newInstance(IncomingCallScreenActivity.class, null, null);

                    Field f = cSubtitleController.getDeclaredField("mHandler");

                    f.setAccessible(true);
                    try {
                        f.set(subtitleInstance, new Handler());
                    }
                    catch (IllegalAccessException e) {}
                    finally {
                        f.setAccessible(false);
                    }

                    Method setsubtitleanchor = mPlayer.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);

                    setsubtitleanchor.invoke(mPlayer, subtitleInstance, null);
                    //Log.e("", "subtitle is setted :p");
                } catch (Exception e) {}

                try {
                    mPlayer.setDataSource(mContext,
                            Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.phone_loud1));
                    mPlayer.prepare();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Could not setup media player for ringtone");
                    mPlayer = null;
                    return;
                }
                mPlayer.setLooping(true);
                mPlayer.start();
                break;
        }
    }

    public void stopRingtone() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void playProgressTone() {
        stopProgressTone();
        try {
            mProgressTone = createProgressTone(mContext);
            mProgressTone.play();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not play progress tone", e);
        }
    }

    public void stopProgressTone() {
        if (mProgressTone != null) {
            mProgressTone.stop();
            mProgressTone.release();
            mProgressTone = null;
        }
    }

    private static AudioTrack createProgressTone(Context context) throws IOException {
        AssetFileDescriptor fd = context.getResources().openRawResourceFd(R.raw.progress_tone);
        int length = (int) fd.getLength();

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);

        byte[] data = new byte[length];
        readFileToBytes(fd, data);

        audioTrack.write(data, 0, data.length);
        audioTrack.setLoopPoints(0, data.length / 2, 30);

        return audioTrack;
    }

    private static void readFileToBytes(AssetFileDescriptor fd, byte[] data) throws IOException {
        FileInputStream inputStream = fd.createInputStream();

        int bytesRead = 0;
        while (bytesRead < data.length) {
            int res = inputStream.read(data, bytesRead, (data.length - bytesRead));
            if (res == -1) {
                break;
            }
            bytesRead += res;
        }
    }
}
