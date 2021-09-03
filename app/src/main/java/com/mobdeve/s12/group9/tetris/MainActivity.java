package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // Debugging tags
    private static final String MUSIC_TAG = "Music";

    // Game components
    private MediaPlayer mPlayer = new MediaPlayer();

    // Main menu view components
    private Button btnPlay;
    private ImageButton btnSettings;
    private ImageButton btnScores;

    // Board view components
    private View gameView;

    // Settings view components
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private double touchSensitivity = 0.5;
    private boolean musicEnabled = true;
    private boolean soundsEnabled = true;

    // References to the music tracks
    String musicStart = String.valueOf(R.raw.tetris_remix_start);
    String musicLoop = String.valueOf(R.raw.tetris_remix_loop);

    /***
     * Activity listeners
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Make app fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize PreferenceManager
        this.sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Set main menu view
        setContentView(R.layout.activity_main);
        this.btnPlay = findViewById(R.id.btn_menu_play);
        this.btnSettings = findViewById(R.id.btn_menu_settings);
        this.btnScores = findViewById(R.id.btn_menu_leaderboard);

        // Plays the Tetris theme
        musicStart();

        // Listen to menu buttons
        bindToPlayButton();
    }

    // Binds the play button to launch the board activity
    private void bindToPlayButton() {
        btnPlay.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, Game.class);
            startActivity(i);
        });
    }

    // Binds the settings button to launch the settings view
    private void bindToSettingsButton() {
        btnSettings.setOnClickListener(v -> {

        });
    }

    /***
     * Music control
     */

    private void setMusicTrack(String trackId, boolean isLooping) {
        // Create path to music track in raw folder
        Uri mediaPath = Uri.parse("android.resource://" + this.getPackageName() + "/" + trackId);
        try {
            mPlayer.setDataSource(getApplicationContext(), mediaPath);
            mPlayer.setLooping(isLooping);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void nextMusicTrack(String nextTrackId, boolean nextIsLooping) {
        mPlayer.stop();
        mPlayer.reset();
        setMusicTrack(nextTrackId, nextIsLooping);
        mPlayer.start();
    }

    // Plays the start track of the music and transitions into the loop track
    public void musicStart() {
        // Set the loop track to play indefinitely after the start track ends
        mPlayer.setOnCompletionListener(mp -> {
            nextMusicTrack(musicLoop, true);
            Log.d(MUSIC_TAG, "\nPlaying loop of Tetris theme\n");
        });

        // Plays the start track
        setMusicTrack(musicStart, false);
        mPlayer.start();
        Log.d(MUSIC_TAG, "\nPlaying start of Tetris theme\n");
    }

    // Pauses any music track in the MediaPlayer
    public void musicPause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            Log.d(MUSIC_TAG, "\nPaused the Tetris theme\n");
        }
    }

    // Resumes any track in the MediaPlayer
    public void musicResume() {
        if (!mPlayer.isPlaying()) {
            // Continue where it left off
            int trackPos = mPlayer.getCurrentPosition();
            mPlayer.seekTo(trackPos);
            mPlayer.start();
            Log.d(MUSIC_TAG, "\nContinued the Tetris theme\n");
        }
    }

    // Stops any music track, plays the end track, and resets the MediaPlayer
    public void musicStop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            Log.d(MUSIC_TAG, "\nStopped the Tetris theme\n");

        }
    }

    /***
     * Life cycle
     */

    @Override
    protected void onPause() {
        super.onPause();
        musicPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        musicPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicResume();
    }

}