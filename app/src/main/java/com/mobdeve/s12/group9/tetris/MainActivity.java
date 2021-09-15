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
    private LinearLayout llMisc;
    private Button btnPlay;
    private Button btnSprint;
    private Button btnMarathon;
    private Button btnEndless;
    private Button btnBack;
    private ImageButton btnSettings;
    private ImageButton btnLeader;

    // View components
    private View settingsView;
    private View leaderView;

    private MusicService musicService;
    private SettingsService settingsService;

    /***
     * Activity listeners
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set main menu view
        setContentView(R.layout.activity_main);

        this.btnPlay = findViewById(R.id.btn_menu_play);
        this.btnSettings = findViewById(R.id.btn_menu_settings);
        this.btnLeader = findViewById(R.id.btn_menu_leaderboard);

        this.btnSprint = findViewById(R.id.btn_play_sprint);
        this.btnMarathon = findViewById(R.id.btn_play_marathon);
        this.btnEndless = findViewById(R.id.btn_play_endless);
        this.btnBack = findViewById(R.id.btn_play_back);

        this.llModes = findViewById(R.id.ll_menu_modes);
        this.llMisc = findViewById(R.id.ll_menu_misc);
        this.clOverlay = findViewById(R.id.cl_menu_overlay_container);

        // Start MusicService, plays the Tetris theme
        musicService = new MusicService(MainActivity.this);
        musicService.start();

        // Listen to menu buttons
        bindMenuButtons();
    }

    /**
     * Listeners and inflaters for the play, settings, and leaderboard buttons
     */
    private void bindMenuButtons() {
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View viewLeader = inflater.inflate(R.layout.activity_leaderboard, null);
        View viewSettings = inflater.inflate(R.layout.activity_settings, null);

        setViewToInflateToLayout(viewLeader);
        setViewToInflateToLayout(viewSettings);

        leaderView = viewLeader;
        settingsView = viewSettings;

        // Set up intent to GameActivity
        Intent i = new Intent(MainActivity.this, GameActivity.class);

        // Hides the play, settings, and leaderboard button to show the game modes
        btnPlay.setOnClickListener(v -> { showGameModes(); });

        // Opens the Settings view
        btnSettings.setOnClickListener(v -> {
            if (settingsView.getParent() == null) {
                clOverlay.addView(settingsView);

                // Initialize SettingsService
                settingsService = new SettingsService(settingsView, clOverlay, MainActivity.this);
            }
        });

        // Opens the Leaderboard view
        btnLeader.setOnClickListener(v -> {
            if (leaderView.getParent() == null) {
                clOverlay.addView(leaderView);
            }
        });

        // Opens the game in different game modes
        btnSprint.setOnClickListener(v -> {
            i.putExtra(GameMode.class.getName(), GameMode.SPRINT.name());
            startActivity(i);
            hideGameModes();
        });

        btnMarathon.setOnClickListener(v -> {
            i.putExtra(GameMode.class.getName(), GameMode.MARATHON.name());
            startActivity(i);
            hideGameModes();
        });

        btnEndless.setOnClickListener(v -> {
            i.putExtra(GameMode.class.getName(), GameMode.ENDLESS.name());
            startActivity(i);
            hideGameModes();
        });

        // Revert back to normal menu
        btnBack.setOnClickListener(v -> { hideGameModes(); });
    }

    /**
     * Hides the Play, Settings, and Leaderboard buttons to show the game mode buttons
     */
    private void showGameModes() {
        btnPlay.setVisibility(View.GONE);
        llMisc.setVisibility(View.GONE);
        llModes.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the game mode buttons to show the standard menu buttons
     */
    private void hideGameModes() {
        btnPlay.setVisibility(View.VISIBLE);
        llMisc.setVisibility(View.VISIBLE);
        llModes.setVisibility(View.GONE);
    }

    /**
     * Sets a View's bounds to match that of a ConstraintLayout
     * @param view A view that will be inflated onto a ConstraintLayout
     */
    private void setViewToInflateToLayout(View view) {
        view.setLayoutParams(new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        );
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