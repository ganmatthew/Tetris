package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

public class MusicService {
    // Debugging tags
    private static final String MUSIC_TAG = "Music";

    // Player components
    private MediaPlayer mPlayer;
    private Context context;

    // References to the music tracks
    private final int musicStart = R.raw.tetris_remix_start;
    private final String musicLoop = String.valueOf(R.raw.tetris_remix_loop);

    public MusicService(Context context) {
        this.context = context;
        mPlayer = MediaPlayer.create(context, musicStart);
    }

    public void start() {
        mPlayer.setOnCompletionListener(mp -> {
            nextTrack(musicLoop, true);
            Log.d(MUSIC_TAG, "\nPlaying loop of Tetris theme\n");
        });

        // Plays the start track
        mPlayer.start();
        Log.d(MUSIC_TAG, "\nPlaying start of Tetris theme\n");
    }

    public void setTrack(String trackId, boolean isLooping) {
        // Create path to music track in raw folder
        Uri mediaPath = Uri.parse("android.resource://" + this.context.getPackageName() + "/" + trackId);
        try {
            mPlayer.setDataSource(this.context.getApplicationContext(), mediaPath);
            mPlayer.setLooping(isLooping);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nextTrack(String nextTrackId, boolean nextIsLooping) {
        mPlayer.stop();
        mPlayer.reset();
        setTrack(nextTrackId, nextIsLooping);
        mPlayer.start();
    }

    // Stops the MediaPlayer and resets it
    public void stop() {
        if (mPlayer != null) {
            try {
                if (mPlayer.isPlaying())
                    mPlayer.stop();
                mPlayer.reset();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
    }

    // Pauses any music track in the MediaPlayer
    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            Log.d(MUSIC_TAG, "\nPaused the Tetris theme\n");
        }
    }

    // Resumes any track in the MediaPlayer
    public void resume() {
        if (!mPlayer.isPlaying()) {
            // Continue where it left off
            mPlayer.seekTo(mPlayer.getCurrentPosition());
            mPlayer.start();
            Log.d(MUSIC_TAG, "\nContinued the Tetris theme\n");
        }
    }
}
