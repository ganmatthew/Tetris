package com.mobdeve.s12.group9.tetris;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
    // Debugging tags
    private static final String GESTURE_TAG = "Gestures";
    private static final String MUSIC_TAG = "Music";

    private GestureDetectorCompat mDetector;

    protected GameView GameView;

    private Button btnPlay;
    private ImageButton btnSettings;
    private ImageButton btnScores;

    private View gameView;

    MediaPlayer mPlayer = new MediaPlayer();

    // References to the music tracks in res
    String musicStart = String.valueOf(R.raw.tetris_remix_start);
    String musicLoop = String.valueOf(R.raw.tetris_remix_loop);

    /***
     * Activity start
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Make app fullscreen
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set main menu view
        setContentView(R.layout.acitivity_main_menu);
        this.btnPlay = findViewById(R.id.btn_menu_play);
        this.btnSettings = findViewById(R.id.btn_menu_settings);
        this.btnScores = findViewById(R.id.btn_menu_settings);

        // Plays the Tetris theme
        musicStart();

        // Initialize game activity
        bindToPlayButton();

        // Set up gesture detection
        mDetector = new GestureDetectorCompat(this,this);
        // Set up listener for double tap events
        mDetector.setOnDoubleTapListener(this);

    }

    // Binds the play button to launch the game proper
    private void bindToPlayButton() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_board);
                ConstraintLayout clBoardLayout = findViewById(R.id.cl_board_layout);
                gameView = new GameView(MainActivity.this);
                clBoardLayout.addView(gameView);
            }
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
     * Touch Events
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) { return false; }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        // Get the median x-coordinate of the display
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        defaultDisplay.getSize(displaySize);

        float medianLine = (float) displaySize.x / 2;

        // Listen for double tap actions on either side
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            float fingerPosition = e.getX();
            if(fingerPosition < medianLine) {
                Log.d(GESTURE_TAG, "\nDouble tap on LEFT SIDE\n");
            } else {
                Log.d(GESTURE_TAG, "\nDouble tap on RIGHT SIDE\n");
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) { return false; }

    @Override
    public boolean onDown(MotionEvent e) { return false; }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) { return false; }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float angle = (float) Math.toDegrees(Math.atan2(e1.getY() - e2.getY(), e2.getX() - e1.getX()));

        if (angle > -45 && angle <= 45) {
            Log.d(GESTURE_TAG, "\nFling from RIGHT to LEFT\n");
            return true;
        }

        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) {
            Log.d(GESTURE_TAG, "\nFling from LEFT to RIGHT\n");
            return true;
        }

        if (angle < -45 && angle >= -135) {
            Log.d(GESTURE_TAG, "\nFling from UP to DOWN\n");
            return true;
        }

        if (angle > 45 && angle <= 135) {
            Log.d(GESTURE_TAG, "\nFling from DOWN to UP\n");
            return true;
        }

        return false;
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