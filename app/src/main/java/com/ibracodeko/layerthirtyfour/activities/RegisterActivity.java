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

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout tilUsername, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etUsername, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister, btnLogin, btnThemeToggle;
    private LottieAnimationView registerAnimation;
    private View loadingOverlay;
    private PreferenceManager prefManager;
    private SupabaseManager supabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initManagers();
        setupClickListeners();
        startAnimation();
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);
        registerAnimation = findViewById(R.id.registerAnimation);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void initManagers() {
        prefManager = new PreferenceManager(this);
        supabaseManager = new SupabaseManager();
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            performRegister();
        });

        btnLogin.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        btnThemeToggle.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            ThemeManager.getInstance().toggleTheme();
            recreate();
        });
    }

    private void startAnimation() {
        registerAnimation.setAnimation("signup_animation.json");
        registerAnimation.playAnimation();
    }

    private void performRegister() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Reset errors
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validate inputs
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username tidak boleh kosong");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email tidak boleh kosong");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError("Format email tidak valid");
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

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Password tidak sama");
            return;
        }

        showLoading(true);

        supabaseManager.signUp(username, email, password, new SupabaseManager.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    showLoading(false);
                    SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
                    Toast.makeText(RegisterActivity.this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show();
                    
                    // Show success animation
                    registerAnimation.setAnimation("email_success.json");
                    registerAnimation.playAnimation();
                    
                    // Navigate to login after delay
                    registerAnimation.postDelayed(() -> {
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }, 2000);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    SoundManager.getInstance().playSound(SoundManager.SOUND_ERROR);
                    Toast.makeText(RegisterActivity.this, "Registrasi gagal: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
    }
}