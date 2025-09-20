package com.ibracodeko.layerthirtyfour.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ibracodeko.layerthirtyfour.R;
import com.ibracodeko.layerthirtyfour.api.SupabaseManager;
import com.ibracodeko.layerthirtyfour.models.User;
import com.ibracodeko.layerthirtyfour.utils.PreferenceManager;
import com.ibracodeko.layerthirtyfour.utils.SoundManager;
import com.ibracodeko.layerthirtyfour.utils.ThemeManager;
import com.ibracodeko.layerthirtyfour.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin, btnRegister, btnThemeToggle;
    private LottieAnimationView loginAnimation;
    private View loadingOverlay;
    private PreferenceManager prefManager;
    private SupabaseManager supabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initManagers();
        setupClickListeners();
        startAnimation();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);
        loginAnimation = findViewById(R.id.loginAnimation);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void initManagers() {
        prefManager = new PreferenceManager(this);
        supabaseManager = new SupabaseManager();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            performLogin();
        });

        btnRegister.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnThemeToggle.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            ThemeManager.getInstance().toggleTheme();
            recreate();
        });
    }

    private void startAnimation() {
        loginAnimation.setAnimation("login_animation.json");
        loginAnimation.playAnimation();
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Reset errors
        tilUsername.setError(null);
        tilPassword.setError(null);

        // Validate inputs
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username tidak boleh kosong");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password tidak boleh kosong");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            tilPassword.setError("Password minimal 6 karakter");
            return;
        }

        showLoading(true);
        
        // Simulate login process (replace with actual Supabase auth)
        supabaseManager.signIn(username, password, new SupabaseManager.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    showLoading(false);
                    prefManager.setLoggedIn(true);
                    prefManager.saveUser(user);
                    
                    SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
                    Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    SoundManager.getInstance().playSound(SoundManager.SOUND_ERROR);
                    Toast.makeText(LoginActivity.this, "Login gagal: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }
}