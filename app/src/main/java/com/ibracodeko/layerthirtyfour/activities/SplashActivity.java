package com.ibracodeko.layerthirtyfour.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.ibracodeko.layerthirtyfour.R;
import com.ibracodeko.layerthirtyfour.utils.PreferenceManager;
import com.ibracodeko.layerthirtyfour.utils.SoundManager;
import com.ibracodeko.layerthirtyfour.utils.ThemeManager;

public class SplashActivity extends AppCompatActivity {
    private LottieAnimationView splashAnimation;
    private static final int SPLASH_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setContentView
        ThemeManager.getInstance().applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        startAnimation();
        playStartupSound();
        navigateToNextScreen();
    }

    private void initViews() {
        splashAnimation = findViewById(R.id.splashAnimation);
    }

    private void startAnimation() {
        splashAnimation.setAnimation("login_animation.json");
        splashAnimation.playAnimation();
        
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        splashAnimation.startAnimation(fadeIn);
    }

    private void playStartupSound() {
        SoundManager.getInstance().playSound(SoundManager.SOUND_STARTUP);
    }

    private void navigateToNextScreen() {
        new Handler().postDelayed(() -> {
            Intent intent;
            
            // Check if user is already logged in
            PreferenceManager prefManager = new PreferenceManager(this);
            if (prefManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, DashboardActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }, SPLASH_DURATION);
    }
}