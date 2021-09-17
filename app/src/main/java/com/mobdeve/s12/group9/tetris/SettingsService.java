package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Observable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.slider.Slider;

enum Keys {
    USERNAME,
    MUSIC_ENABLED,
    SOUNDS_ENABLED,
    TOUCH_SENSITIVITY
}

public class SettingsService {

    // SharedPreferences components
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    /*
    private float touchSensitivity = 4;
    private boolean musicEnabled = true;
    private boolean soundsEnabled = true;
    */

    private View settingsView;

    private EditText etUsername;
    private Switch musicSwitch;
    private Slider touchSensSlider;
    private ImageButton btnClose;
    private ConstraintLayout clOverlay;

    private Context context;

    private boolean isViewInflated;

    public SettingsService(View view, ConstraintLayout layout, Context context) {
        this.settingsView = view;
        this.clOverlay = layout;
        this.context = context;

        isViewInflated = true;

        etUsername = view.findViewById(R.id.et_settings_username);
        musicSwitch = view.findViewById(R.id.sw_settings_music);
        touchSensSlider = view.findViewById(R.id.sl_settings_sensitivity);
        btnClose = view.findViewById(R.id.btn_settings_exit);

        this.sp = PreferenceManager.getDefaultSharedPreferences(( context.getApplicationContext() ));
        this.spEditor = this.sp.edit();

        this.loadSettings();
        this.closeSettings();
    }

    // Sets the settings parameters based on the loaded data
    public void loadSettings() {
        this.etUsername.setText( this.sp.getString( Keys.USERNAME.name(), this.etUsername.getText().toString() ) );
        this.musicSwitch.setChecked( this.sp.getBoolean( Keys.MUSIC_ENABLED.name(), this.musicSwitch.isChecked() ) );
        this.touchSensSlider.setValue( this.sp.getFloat( Keys.TOUCH_SENSITIVITY.name(), this.touchSensSlider.getValue() ) );
    }

    // Saves a backup of the data to SharedPreferences
    public void saveSettings() {
        this.spEditor.putString( Keys.USERNAME.name(), this.etUsername.getText().toString() );
        this.spEditor.putBoolean( Keys.MUSIC_ENABLED.name(), this.musicSwitch.isChecked() );
        this.spEditor.putFloat( Keys.TOUCH_SENSITIVITY.name(), this.touchSensSlider.getValue() );

        this.spEditor.apply();
    }

    public void closeSettings() {
        btnClose.setOnClickListener(v -> {
            // Save any changes to Settings first
            saveSettings();
            // Close settings, update boolean
            clOverlay.removeView(settingsView);
            isViewInflated = false;
        });
    }

    // Changes the context of settings as it is passed from one activity to another
    public void setContext(Context context) { this.context = context; }

    public String getUsername() { return this.sp.getString( Keys.USERNAME.name(), this.etUsername.getText().toString() ); }

    public boolean getMusicEnabled() { return this.sp.getBoolean( Keys.MUSIC_ENABLED.name(), this.musicSwitch.isChecked() ); }

    public float getSensitivity() { return this.sp.getFloat( Keys.TOUCH_SENSITIVITY.name(), this.touchSensSlider.getValue() ); }

    public boolean getIsInflated() { return this.isViewInflated; }
}
