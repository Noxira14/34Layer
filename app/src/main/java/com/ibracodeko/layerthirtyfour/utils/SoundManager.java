package com.ibracodeko.layerthirtyfour.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import com.ibracodeko.layerthirtyfour.R;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static SoundManager instance;
    private SoundPool soundPool;
    private Map<Integer, Integer> soundMap;
    private Context context;
    private Vibrator vibrator;
    private boolean soundEnabled = true;
    private boolean vibrationEnabled = true;

    // Sound constants
    public static final int SOUND_CLICK = 1;
    public static final int SOUND_SUCCESS = 2;
    public static final int SOUND_ERROR = 3;
    public static final int SOUND_NOTIFICATION = 4;
    public static final int SOUND_STARTUP = 5;

    private SoundManager() {
        soundMap = new HashMap<>();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        this.context = context;
        
        // Initialize SoundPool
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build();

        soundPool = new SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build();

        // Load sounds
        loadSounds();

        // Initialize vibrator
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void loadSounds() {
        // Load sound effects (you need to add these to res/raw folder)
        soundMap.put(SOUND_CLICK, soundPool.load(context, R.raw.click_sound, 1));
        soundMap.put(SOUND_SUCCESS, soundPool.load(context, R.raw.success_sound, 1));
        soundMap.put(SOUND_ERROR, soundPool.load(context, R.raw.error_sound, 1));
        soundMap.put(SOUND_NOTIFICATION, soundPool.load(context, R.raw.notification_sound, 1));
        soundMap.put(SOUND_STARTUP, soundPool.load(context, R.raw.startup_sound, 1));
    }

    public void playSound(int soundId) {
        if (!soundEnabled) return;

        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            float volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / 
                          (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            
            Integer soundResourceId = soundMap.get(soundId);
            if (soundResourceId != null) {
                soundPool.play(soundResourceId, volume, volume, 1, 0, 1.0f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void vibrate(long duration) {
        if (!vibrationEnabled || vibrator == null) return;

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(android.os.VibrationEffect.createOneShot(duration, 
                    android.os.VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(duration);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void vibrateClick() {
        vibrate(50);
    }

    public void vibrateSuccess() {
        vibrate(100);
    }

    public void vibrateError() {
        vibrate(200);
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public void setVibrationEnabled(boolean enabled) {
        this.vibrationEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}