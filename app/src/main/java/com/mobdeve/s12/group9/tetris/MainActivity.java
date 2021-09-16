package com.mobdeve.s12.group9.tetris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

enum IntentKeys {
    SAVED_GAME_PRESENT,
    ADD_LEADERBOARD
}

public class MainActivity extends AppCompatActivity {

    // Main menu view components
    private ConstraintLayout clOverlay;
    private LinearLayout llModes;
    private LinearLayout llMisc;
    private Button btnPlay;
    private Button btnContinue;
    private Button btnSprint;
    private Button btnMarathon;
    private Button btnEndless;
    private Button btnBack;
    private ImageButton btnSettings;
    private ImageButton btnLeader;

    private boolean enableBtnContinue = false;

    // View components
    private View settingsView;
    private View leaderView;

    private MusicService musicService;
    private SettingsService settingsService;

    private ImageButton btnLeaderExit;

    private RecyclerView leaderRecyclerView;
    private LeaderboardAdapter leaderAdapter;
    private ArrayList<LeaderboardEntry> leaderData = new ArrayList<LeaderboardEntry>();

    /***
     * Activity listeners
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set main menu view
        setContentView(R.layout.activity_main);

        this.btnPlay = findViewById(R.id.btn_menu_play);
        this.btnContinue = findViewById(R.id.btn_menu_continue);
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

        btnLeaderExit = viewLeader.findViewById(R.id.btn_leaderboard_exit);

        // Initialize SettingsService
        settingsService = new SettingsService(settingsView, clOverlay, MainActivity.this);

        // Set up intent to GameActivity
        Intent i = new Intent(MainActivity.this, GameActivity.class);

        // Continue mode will prompt GameActivity to load the first entry from the database
        if (enableBtnContinue) {
            btnContinue.setVisibility(View.VISIBLE);
            btnContinue.setOnClickListener(v -> {
                i.putExtra(GameMode.class.getName(), GameMode.CONTINUE.name());
                startActivityForResult(i, 1);
                hideGameModes();
            });
        } else {
            btnContinue.setVisibility(View.GONE);
        }

        // Hides the play, settings, and leaderboard button to show the game modes
        btnPlay.setOnClickListener(v -> { showGameModes(); });

        // Opens the Settings view
        btnSettings.setOnClickListener(v -> {
            if (settingsView.getParent() == null) {
                clOverlay.addView(settingsView);
            }
        });

        // Opens the Leaderboard view
        btnLeader.setOnClickListener(v -> {
            if (leaderView.getParent() == null) {
                clOverlay.addView(leaderView);

                TextView tvLeaderEmpty = leaderView.findViewById(R.id.tv_leaderboard_empty);
                leaderRecyclerView = leaderView.findViewById(R.id.rv_leaderboard);

                if (!leaderData.isEmpty()) {
                    tvLeaderEmpty.setVisibility(View.GONE);
                    leaderAdapter = new LeaderboardAdapter(leaderData);
                    leaderRecyclerView.setAdapter(leaderAdapter);

                    leaderAdapter.notifyItemChanged(0);
                    leaderAdapter.notifyItemRangeChanged(0, leaderAdapter.getItemCount());
                } else {
                    tvLeaderEmpty.setVisibility(View.VISIBLE);
                }
            }
        });

        // Closes the Leaderboard view
        btnLeaderExit.setOnClickListener(v -> {
            if (leaderView.getParent() != null) {
                clOverlay.removeView(leaderView);
            }
        });

        // Opens the game in different game modes
        btnSprint.setOnClickListener(v -> {
            i.putExtra(GameMode.class.getName(), GameMode.SPRINT.name());
            startActivityForResult(i, 1);
            hideGameModes();
        });

        btnMarathon.setOnClickListener(v -> {
            i.putExtra(GameMode.class.getName(), GameMode.MARATHON.name());
            startActivityForResult(i, 1);
            hideGameModes();
        });

        btnEndless.setOnClickListener(v -> {
            i.putExtra(GameMode.class.getName(), GameMode.ENDLESS.name());
            startActivityForResult(i, 1);
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
     * Recevies intents from other activities
     * @param requestCode Request code from the ActivityResult.
     * @param resultCode Result code from the ActivityResult.
     * @param data Holds the intents that will be passed from GameActivity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK) {
                    // Indicates that there is a saved game to be continued
                    enableBtnContinue = data.getBooleanExtra(IntentKeys.SAVED_GAME_PRESENT.name(), false);
                    // Adds new entry to leaderboard
                    String json = data.getStringExtra(IntentKeys.ADD_LEADERBOARD.name());
                    leaderData.add( new Gson().fromJson(json, LeaderboardEntry.class) );
                }
                break;
            }
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