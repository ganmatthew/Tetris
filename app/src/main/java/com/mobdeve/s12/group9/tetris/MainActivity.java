package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    // Debugging tags
    private static final String MUSIC_TAG = "Music";

    // Game components
    private MediaPlayer mPlayer = new MediaPlayer();

    // Main menu view components
    private ConstraintLayout clOverlay;
    private LinearLayout llModes;
    private Button btnPlay;
    private ImageButton btnSettings;
    private ImageButton btnLeader;
    private View settingsView;
    private View leaderView;

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

        // Initialize PreferenceManager
        this.sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Set main menu view
        setContentView(R.layout.activity_main);

        this.btnPlay = findViewById(R.id.btn_menu_play);
        this.btnSettings = findViewById(R.id.btn_menu_settings);
        this.btnLeader = findViewById(R.id.btn_menu_leaderboard);

        this.llModes = findViewById(R.id.ll_menu_modes);
        this.clOverlay = findViewById(R.id.cl_menu_overlay_container);

        // Plays the Tetris theme
        musicStart();

        // Listen to menu buttons
        bindToPlayButton();
        settingsView = bindViewToButton(R.layout.activity_settings, R.id.btn_settings_exit, btnSettings);
        leaderView = bindViewToButton(R.layout.activity_leaderboard, R.id.btn_leaderboard_exit, btnLeader);
    }

    // Binds the play button to launch the board activity
    private void bindToPlayButton() {
        btnPlay.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            startActivity(i);
            /*
            if (btnPlay.getVisibility() == View.VISIBLE) {
                llModes.setVisibility(View.VISIBLE);
                btnPlay.setVisibility(View.GONE);
            }
            */
        });
    }

    // Binds a given view to a button
    private View bindViewToButton(int viewRes, int closeButtonRes, ImageButton openButton) {
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View view = inflater.inflate(viewRes, null);

        openButton.setOnClickListener(v -> {
            if (view.getParent() == null)
                clOverlay.addView(view);
        });

        ImageButton closeButton = view.findViewById(closeButtonRes);
        view.setLayoutParams(new ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        );

        closeButton.setOnClickListener(v -> {
            if (view.getParent() != null)
                clOverlay.removeView(view);
        });

        return view;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (this.settingsView.getParent() != null) {
            clOverlay.removeView(this.settingsView);
        }

        if (this.leaderView.getParent() != null) {
            clOverlay.removeView(this.leaderView);
        }
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