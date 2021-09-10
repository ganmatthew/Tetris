package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    // Main menu view components
    private ConstraintLayout clOverlay;
    private LinearLayout llModes;
    private Button btnPlay;
    private ImageButton btnSettings;
    private ImageButton btnLeader;

    // View components
    private View settingsView;
    private View leaderView;

    private MusicService musicService;

    // Settings view components
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private double touchSensitivity = 0.5;
    private boolean musicEnabled = true;
    private boolean soundsEnabled = true;

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

        // Start MusicService, plays the Tetris theme
        musicService = new MusicService(MainActivity.this);
        musicService.start();

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

    /**
     * Binds a given view to respective open and close buttons
     * @param viewRes the View that will be opened/closed
     * @param closeButtonRes Res to the Button to close the View
     * @param openButton Res to the ImageButton to open the View
     * @return Returns the view that was opened
     */
    private View bindViewToButton(int viewRes, int closeButtonRes, ImageButton openButton) {
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View view = inflater.inflate(viewRes, null);
        view.setLayoutParams(new ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        );

        openButton.setOnClickListener(v -> {
            if (view.getParent() == null)
                clOverlay.addView(view);
        });

        ImageButton closeButton = view.findViewById(closeButtonRes);

        closeButton.setOnClickListener(v -> {
            if (view.getParent() != null)
                clOverlay.removeView(view);
        });

        return view;
    }

    /**
     *  Forces the app into immersive fullscreen
     */
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

    /**
     * Overrides the back button to close the settings/leaderboard view
     */
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
     * Life cycle
     */

    @Override
    protected void onPause() {
        super.onPause();
        if (musicService != null) {
            musicService.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicService != null) {
            musicService.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicService != null) {
            musicService.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicService != null) {
            musicService.resume();
        }
    }

}