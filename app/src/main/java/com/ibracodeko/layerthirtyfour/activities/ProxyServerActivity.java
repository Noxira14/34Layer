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

public class ProxyServerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextInputLayout tilDomain, tilTime, tilProxy, tilMethod;
    private TextInputEditText etDomain, etTime;
    private AutoCompleteTextView etProxy;
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
        setContentView(R.layout.activity_proxy_server);

        initViews();
        initManagers();
        setupToolbar();
        setupClickListeners();
        loadMethods();
        loadProxyList();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tilDomain = findViewById(R.id.tilDomain);
        tilTime = findViewById(R.id.tilTime);
        tilProxy = findViewById(R.id.tilProxy);
        tilMethod = findViewById(R.id.tilMethod);
        etDomain = findViewById(R.id.etDomain);
        etTime = findViewById(R.id.etTime);
        etProxy = findViewById(R.id.etProxy);
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
        getSupportActionBar().setTitle("Proxy Server");
        
        toolbar.setNavigationOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            onBackPressed();
        });
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            performProxyTest();
        });

        btnExport.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            exportResults();
        });
    }

    private void loadMethods() {
        apiService.getMethods("proxy_server").enqueue(new Callback<MethodResponse>() {
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

    private void loadProxyList() {
        // Setup common proxy servers for testing
        List<String> proxies = getCommonProxies();
        ArrayAdapter<String> proxyAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, proxies);
        
        // Convert EditText to AutoCompleteTextView for proxy suggestions
        if (etProxy instanceof AutoCompleteTextView) {
            ((AutoCompleteTextView) etProxy).setAdapter(proxyAdapter);
        }
    }

    private List<String> getDefaultMethods() {
        List<String> methods = new ArrayList<>();
        methods.add("ProxyTest");
        methods.add("ProxySpeed");
        methods.add("ProxyAnonymity");
        methods.add("ProxyConnection");
        return methods;
    }

    private List<String> getCommonProxies() {
        List<String> proxies = new ArrayList<>();
        proxies.add("8.8.8.8:80");
        proxies.add("1.1.1.1:80");
        proxies.add("proxy.example.com:8080");
        proxies.add("free-proxy.cz:8080");
        return proxies;
    }

    private void setupMethodDropdown(List<String> methods) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, methods);
        actvMethod.setAdapter(adapter);
        
        if (!methods.isEmpty()) {
            actvMethod.setText(methods.get(0), false);
        }
    }

    private void performProxyTest() {
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
        String proxy = etProxy.getText().toString().trim();
        String method = actvMethod.getText().toString().trim();

        // Reset errors
        tilDomain.setError(null);
        tilTime.setError(null);
        tilProxy.setError(null);
        tilMethod.setError(null);

        // Validate inputs
        if (TextUtils.isEmpty(domain)) {
            tilDomain.setError("Domain tidak boleh kosong");
            return;
        }

        if (!ValidationUtils.isValidUrl(domain)) {
            tilDomain.setError("Format domain tidak valid");
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

        if (TextUtils.isEmpty(proxy)) {
            tilProxy.setError("Proxy tidak boleh kosong");
            return;
        }

        if (!isValidProxyFormat(proxy)) {
            tilProxy.setError("Format proxy tidak valid (host:port)");
            return;
        }

        if (TextUtils.isEmpty(method)) {
            tilMethod.setError("Method tidak boleh kosong");
            return;
        }

        showLoading(true);
        lastRequestTime = currentTime;

        // Show legal warning
        Toast.makeText(this, "Peringatan: Gunakan proxy yang sah dan legal!", 
            Toast.LENGTH_LONG).show();

        // Create custom request with proxy parameter
        ProxyTestRequest request = new ProxyTestRequest(domain, time, method, proxy);
        
        performProxyApiCall(request);
    }

    private boolean isValidProxyFormat(String proxy) {
        // Basic validation for proxy format (host:port)
        if (proxy.contains(":")) {
            String[] parts = proxy.split(":");
            if (parts.length == 2) {
                try {
                    int port = Integer.parseInt(parts[1]);
                    return port > 0 && port <= 65535;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return false;
    }

    private void performProxyApiCall(ProxyTestRequest request) {
    // Since we need to pass proxy parameter, we'll use regular scan but with proxy in target
    String targetWithProxy = request.getTarget() + "?proxy=" + request.getProxy();
    String key = "qwertyuiop";
    apiService.performScan(targetWithProxy, request.getTime(), request.getMethod(), key).enqueue(new Callback<ScanResponse>() {
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
                        Toast.makeText(ProxyServerActivity.this, 
                            "Proxy test gagal: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ScanResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    showLoading(false);
                    SoundManager.getInstance().playSound(SoundManager.SOUND_ERROR);
                    Toast.makeText(ProxyServerActivity.this, 
                        "Proxy test gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
        ResultDisplayHelper.showProxyServerResult(this, resultContainer, result);
        resultContainer.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Proxy test selesai! Status: " + result.getStatus(), 
            Toast.LENGTH_LONG).show();
    }

    private void exportResults() {
        if (lastScanResult != null) {
            ResultDisplayHelper.exportScanResult(this, lastScanResult, "Proxy_Server");
            SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
        } else {
            Toast.makeText(this, "Tidak ada hasil untuk diekspor", Toast.LENGTH_SHORT).show();
        }
    }

    // Custom request class for proxy testing
    private static class ProxyTestRequest {
        private String target;
        private int time;
        private String method;
        private String proxy;

        public ProxyTestRequest(String target, int time, String method, String proxy) {
            this.target = target;
            this.time = time;
            this.method = method;
            this.proxy = proxy;
        }

        public String getTarget() { return target; }
        public int getTime() { return time; }
        public String getMethod() { return method; }
        public String getProxy() { return proxy; }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}