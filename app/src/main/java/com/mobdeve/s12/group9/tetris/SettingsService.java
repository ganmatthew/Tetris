package com.mobdeve.s12.group9.tetris;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.slider.Slider;

enum Keys {
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

    private Switch musicSwitch;
    private Switch soundsSwitch;
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

        musicSwitch = view.findViewById(R.id.sw_settings_music);
        soundsSwitch = view.findViewById(R.id.sw_settings_sounds);
        touchSensSlider = view.findViewById(R.id.sl_settings_sensitivity);
        btnClose = view.findViewById(R.id.btn_settings_exit);

        this.sp = PreferenceManager.getDefaultSharedPreferences(( context.getApplicationContext() ));
        this.spEditor = this.sp.edit();

        this.loadSettings();
        this.closeSettings();
    }

    // Sets the settings parameters based on the loaded data
    public void loadSettings() {
        this.musicSwitch.setChecked( this.sp.getBoolean( Keys.MUSIC_ENABLED.name(), this.musicSwitch.isChecked() ) );
        this.soundsSwitch.setChecked( this.sp.getBoolean( Keys.SOUNDS_ENABLED.name(), this.soundsSwitch.isChecked() ) );
        this.touchSensSlider.setValue( this.sp.getFloat( Keys.TOUCH_SENSITIVITY.name(), this.touchSensSlider.getValue() ) );
    }

    // Saves a backup of the data to SharedPreferences
    public void saveSettings() {
        this.spEditor.putBoolean( Keys.MUSIC_ENABLED.name(), this.musicSwitch.isChecked() );
        this.spEditor.putBoolean( Keys.SOUNDS_ENABLED.name(), this.soundsSwitch.isChecked() );
        this.spEditor.putFloat( Keys.TOUCH_SENSITIVITY.name(), this.touchSensSlider.getValue() );

        this.spEditor.apply();
    }

    public void closeSettings() {
        btnClose.setOnClickListener(v -> {
            clOverlay.removeView(settingsView);
            isViewInflated = false;
        });
    }

    // Changes the context of settings as it is passed from one activity to another
    public void setContext(Context context) { this.context = context; }

    public boolean getIsInflated() { return this.isViewInflated; }
}
