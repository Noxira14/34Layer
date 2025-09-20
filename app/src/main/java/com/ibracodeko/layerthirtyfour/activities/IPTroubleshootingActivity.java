package com.ibracodeko.layerthirtyfour.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ibracodeko.layerthirtyfour.R;
import com.ibracodeko.layerthirtyfour.api.ApiClient;
import com.ibracodeko.layerthirtyfour.api.ApiService;
import com.ibracodeko.layerthirtyfour.models.MethodResponse;
import com.ibracodeko.layerthirtyfour.models.ScanRequest;
import com.ibracodeko.layerthirtyfour.models.ScanResponse;
import com.ibracodeko.layerthirtyfour.utils.PreferenceManager;
import com.ibracodeko.layerthirtyfour.utils.SoundManager;
import com.ibracodeko.layerthirtyfour.utils.ThemeManager;
import com.ibracodeko.layerthirtyfour.utils.ValidationUtils;
import com.ibracodeko.layerthirtyfour.utils.ResultDisplayHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import java.util.ArrayList;

public class IPTroubleshootingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputLayout tilDomain, tilTime, tilMethod;
    private TextInputEditText etDomain, etTime;
    private AutoCompleteTextView actvMethod;
    private MaterialButton btnSubmit, btnExport;
    private LottieAnimationView loadingAnimation;
    private View loadingOverlay, resultContainer;
    private PreferenceManager prefManager;
    private ApiService apiService;
    private long lastRequestTime = 0;
    private static final long RATE_LIMIT_DELAY = 60000; // 1 minute
    private ScanResponse lastScanResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_troubleshooting);

        initViews();
        initManagers();
        setupToolbar();
        setupClickListeners();
        loadMethods();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilDomain = findViewById(R.id.tilDomain);
        tilTime = findViewById(R.id.tilTime);
        tilMethod = findViewById(R.id.tilMethod);
        etDomain = findViewById(R.id.etDomain);
        etTime = findViewById(R.id.etTime);
        actvMethod = findViewById(R.id.actvMethod);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnExport = findViewById(R.id.btnExport);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        resultContainer = findViewById(R.id.resultContainer);
    }

    private void initManagers() {
        prefManager = new PreferenceManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("IP Troubleshooting");
        
        toolbar.setNavigationOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            onBackPressed();
        });
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            performIPTroubleshooting();
        });

        btnExport.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            exportResults();
        });
    }

    private void loadMethods() {
        apiService.getMethods("ip_troubleshooting").enqueue(new Callback<MethodResponse>() {
            @Override
            public void onResponse(Call<MethodResponse> call, Response<MethodResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> methods = response.body().getMethods();
                    if (methods != null && !methods.isEmpty()) {
                        setupMethodDropdown(methods);
                    } else {
                        setupMethodDropdown(getDefaultMethods());
                    }
                } else {
                    setupMethodDropdown(getDefaultMethods());
                }
            }

            @Override
            public void onFailure(Call<MethodResponse> call, Throwable t) {
                setupMethodDropdown(getDefaultMethods());
            }
        });
    }

    private List<String> getDefaultMethods() {
        List<String> methods = new ArrayList<>();
        methods.add("PingTest");
        methods.add("TraceRoute");
        methods.add("DNSLookup");
        methods.add("PortScan");
        methods.add("NetworkDiagnosis");
        return methods;
    }

    private void setupMethodDropdown(List<String> methods) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, methods);
        actvMethod.setAdapter(adapter);
        
        if (!methods.isEmpty()) {
            actvMethod.setText(methods.get(0), false);
        }
    }

    private void performIPTroubleshooting() {
        // Check rate limiting
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime < RATE_LIMIT_DELAY) {
            long remainingTime = (RATE_LIMIT_DELAY - (currentTime - lastRequestTime)) / 1000;
            Toast.makeText(this, "Harap tunggu " + remainingTime + " detik sebelum request berikutnya", 
                Toast.LENGTH_LONG).show();
            return;
        }

        String domain = etDomain.getText().toString().trim();
        String timeStr = etTime.getText().toString().trim();
        String method = actvMethod.getText().toString().trim();

        // Reset errors
        tilDomain.setError(null);
        tilTime.setError(null);
        tilMethod.setError(null);

        // Validate inputs
        if (TextUtils.isEmpty(domain)) {
            tilDomain.setError("Domain/IP tidak boleh kosong");
            return;
        }

        if (!ValidationUtils.isValidUrl(domain) && !ValidationUtils.isValidIpAddress(domain)) {
            tilDomain.setError("Format domain/IP tidak valid");
            return;
        }

        if (TextUtils.isEmpty(timeStr)) {
            tilTime.setError("Waktu tidak boleh kosong");
            return;
        }

        int time;
        try {
            time = Integer.parseInt(timeStr);
            if (time < 1 || time > 300) {
                tilTime.setError("Waktu harus antara 1-300 detik");
                return;
            }
        } catch (NumberFormatException e) {
            tilTime.setError("Format waktu tidak valid");
            return;
        }

        if (TextUtils.isEmpty(method)) {
            tilMethod.setError("Method tidak boleh kosong");
            return;
        }

        showLoading(true);
        lastRequestTime = currentTime;

        // Show legal warning
        Toast.makeText(this, "Peringatan: Hanya test domain/IP yang Anda miliki atau memiliki izin!", 
            Toast.LENGTH_LONG).show();

        ScanRequest request = new ScanRequest(domain, time, method);
        
        apiService.performScan(request).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(Call<ScanResponse> call, Response<ScanResponse> response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        lastScanResult = response.body();
                        SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
                        showResult(lastScanResult);
                        btnExport.setVisibility(View.VISIBLE);
                    } else {
                        SoundManager.getInstance().playSound(SoundManager.SOUND_ERROR);
                        Toast.makeText(IPTroubleshootingActivity.this, 
                            "IP Troubleshooting gagal: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ScanResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    showLoading(false);
                    SoundManager.getInstance().playSound(SoundManager.SOUND_ERROR);
                    Toast.makeText(IPTroubleshootingActivity.this, 
                        "IP Troubleshooting gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!show);
        
        if (show) {
            loadingAnimation.setAnimation("loading_animation.json");
            loadingAnimation.playAnimation();
        } else {
            loadingAnimation.cancelAnimation();
        }
    }

    private void showResult(ScanResponse result) {
        ResultDisplayHelper.showIPTroubleshootingResult(this, resultContainer, result);
        resultContainer.setVisibility(View.VISIBLE);
        
        // Show success animation
        loadingAnimation.setAnimation("email_success.json");
        loadingAnimation.playAnimation();
        
        Toast.makeText(this, "IP Troubleshooting selesai! Status: " + result.getStatus(), 
            Toast.LENGTH_LONG).show();
    }

    private void exportResults() {
        if (lastScanResult != null) {
            ResultDisplayHelper.exportScanResult(this, lastScanResult, "IP_Troubleshooting");
            SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
        } else {
            Toast.makeText(this, "Tidak ada hasil untuk diekspor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}