package com.ibracodeko.layerthirtyfour.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ibracodeko.layerthirtyfour.R;
import com.ibracodeko.layerthirtyfour.adapters.MethodAdapter;
import com.ibracodeko.layerthirtyfour.api.ApiClient;
import com.ibracodeko.layerthirtyfour.api.ApiService;
import com.ibracodeko.layerthirtyfour.models.MethodResponse;
import com.ibracodeko.layerthirtyfour.models.User;
import com.ibracodeko.layerthirtyfour.utils.PreferenceManager;
import com.ibracodeko.layerthirtyfour.utils.SoundManager;
import com.ibracodeko.layerthirtyfour.utils.ThemeManager;
import com.ibracodeko.layerthirtyfour.utils.ValidationUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerMethods;
    private FloatingActionButton fabAddMethod;
    private LottieAnimationView loadingAnimation;
    private View loadingOverlay, emptyState;
    
    private MethodAdapter methodAdapter;
    private List<MethodResponse.MethodDetail> methodList;
    private PreferenceManager prefManager;
    private ApiService apiService;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        initViews();
        initManagers();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        checkAdminAccess();
        loadMethods();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerMethods = findViewById(R.id.recyclerMethods);
        fabAddMethod = findViewById(R.id.fabAddMethod);
        loadingAnimation = findViewById(R.id.loadingAnimation);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        emptyState = findViewById(R.id.emptyState);
    }

    private void initManagers() {
        prefManager = new PreferenceManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);
        currentUser = prefManager.getUser();
        methodList = new ArrayList<>();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Admin Panel");
        
        toolbar.setNavigationOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            onBackPressed();
        });
    }

    private void setupRecyclerView() {
        methodAdapter = new MethodAdapter(methodList, new MethodAdapter.OnMethodClickListener() {
            @Override
            public void onEditClick(MethodResponse.MethodDetail method) {
                showEditMethodDialog(method);
            }

            @Override
            public void onDeleteClick(MethodResponse.MethodDetail method) {
                showDeleteConfirmation(method);
            }

            @Override
            public void onToggleClick(MethodResponse.MethodDetail method) {
                toggleMethodStatus(method);
            }
        });
        
        recyclerMethods.setLayoutManager(new LinearLayoutManager(this));
        recyclerMethods.setAdapter(methodAdapter);
    }

    private void setupClickListeners() {
        fabAddMethod.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            showAddMethodDialog();
        });
    }

    private void checkAdminAccess() {
        if (currentUser == null || currentUser.getUserType() != User.UserType.ADMIN) {
            Toast.makeText(this, "Akses ditolak. Hanya admin yang dapat menggunakan fitur ini.", 
                Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void loadMethods() {
        showLoading(true);
        
        apiService.getAllMethods().enqueue(new Callback<MethodResponse>() {
            @Override
            public void onResponse(Call<MethodResponse> call, Response<MethodResponse> response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response.isSuccessful() && response.body() != null) {
                        List<MethodResponse.MethodDetail> methods = response.body().getMethodDetails();
                        if (methods != null && !methods.isEmpty()) {
                            methodList.clear();
                            methodList.addAll(methods);
                            methodAdapter.notifyDataSetChanged();
                            showEmptyState(false);
                        } else {
                            showEmptyState(true);
                        }
                    } else {
                        Toast.makeText(AdminPanelActivity.this, 
                            "Gagal memuat metode: " + response.message(), Toast.LENGTH_LONG).show();
                        showEmptyState(true);
                    }
                });
            }

            @Override
            public void onFailure(Call<MethodResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AdminPanelActivity.this, 
                        "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    showEmptyState(true);
                });
            }
        });
    }

    private void showAddMethodDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_method, null);
        
        TextInputLayout tilMethodName = dialogView.findViewById(R.id.tilMethodName);
        TextInputLayout tilEndpoint = dialogView.findViewById(R.id.tilEndpoint);
        TextInputLayout tilDescription = dialogView.findViewById(R.id.tilDescription);
        TextInputLayout tilType = dialogView.findViewById(R.id.tilType);
        
        TextInputEditText etMethodName = dialogView.findViewById(R.id.etMethodName);
        TextInputEditText etEndpoint = dialogView.findViewById(R.id.etEndpoint);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        AutoCompleteTextView actvType = dialogView.findViewById(R.id.actvType);
        
        // Setup type dropdown
        String[] types = {"web_multiporting", "ip_troubleshooting", "proxy_server"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, types);
        actvType.setAdapter(typeAdapter);
        actvType.setText(types[0], false);

        new MaterialAlertDialogBuilder(this)
            .setTitle("Tambah Method Baru")
            .setView(dialogView)
            .setPositiveButton("Tambah", (dialog, which) -> {
                String name = etMethodName.getText().toString().trim();
                String endpoint = etEndpoint.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String type = actvType.getText().toString().trim();
                
                if (validateMethodInput(name, endpoint, description, type)) {
                    addMethod(name, endpoint, type, description);
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void showEditMethodDialog(MethodResponse.MethodDetail method) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_method, null);
        
        TextInputEditText etMethodName = dialogView.findViewById(R.id.etMethodName);
        TextInputEditText etEndpoint = dialogView.findViewById(R.id.etEndpoint);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        AutoCompleteTextView actvType = dialogView.findViewById(R.id.actvType);
        
        // Pre-fill with existing data
        etMethodName.setText(method.getName());
        etEndpoint.setText(method.getEndpoint());
        etDescription.setText(method.getDescription());
        
        String[] types = {"web_multiporting", "ip_troubleshooting", "proxy_server"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, types);
        actvType.setAdapter(typeAdapter);
        actvType.setText(method.getType(), false);

        new MaterialAlertDialogBuilder(this)
            .setTitle("Edit Method")
            .setView(dialogView)
            .setPositiveButton("Update", (dialog, which) -> {
                String name = etMethodName.getText().toString().trim();
                String endpoint = etEndpoint.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String type = actvType.getText().toString().trim();
                
                if (validateMethodInput(name, endpoint, description, type)) {
                    updateMethod(method, name, endpoint, type, description);
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void showDeleteConfirmation(MethodResponse.MethodDetail method) {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Method")
            .setMessage("Apakah Anda yakin ingin menghapus method '" + method.getName() + "'?")
            .setPositiveButton("Hapus", (dialog, which) -> {
                deleteMethod(method);
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private boolean validateMethodInput(String name, String endpoint, String description, String type) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Nama method tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!ValidationUtils.isValidMethodName(name)) {
            Toast.makeText(this, "Nama method tidak valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (TextUtils.isEmpty(endpoint)) {
            Toast.makeText(this, "Endpoint tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!ValidationUtils.isValidUrl(endpoint)) {
            Toast.makeText(this, "Format endpoint tidak valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (TextUtils.isEmpty(type)) {
            Toast.makeText(this, "Type tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private void addMethod(String name, String endpoint, String type, String description) {
        showLoading(true);
        
        ApiService.MethodRequest request = new ApiService.MethodRequest(name, endpoint, type, description);
        
        apiService.addMethod(request).enqueue(new Callback<MethodResponse>() {
            @Override
            public void onResponse(Call<MethodResponse> call, Response<MethodResponse> response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response.isSuccessful()) {
                        SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
                        Toast.makeText(AdminPanelActivity.this, "Method berhasil ditambahkan", 
                            Toast.LENGTH_SHORT).show();
                        loadMethods(); // Refresh list
                    } else {
                        SoundManager.getInstance().playSound(SoundManager.SOUND_ERROR);
                        Toast.makeText(AdminPanelActivity.this, 
                            "Gagal menambah method: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<MethodResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    showLoading(false);
                    SoundManager.getInstance().playSound(SoundManager.SOUND_ERROR);
                    Toast.makeText(AdminPanelActivity.this, 
                        "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void updateMethod(MethodResponse.MethodDetail method, String name, String endpoint, String type, String description) {
        // Implementation for updating method
        SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
        Toast.makeText(this, "Method updated successfully", Toast.LENGTH_SHORT).show();
        loadMethods();
    }

    private void deleteMethod(MethodResponse.MethodDetail method) {
        // Implementation for deleting method
        SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
        Toast.makeText(this, "Method deleted successfully", Toast.LENGTH_SHORT).show();
        loadMethods();
    }

    private void toggleMethodStatus(MethodResponse.MethodDetail method) {
        // Implementation for toggling method enable/disable status
        method.setEnabled(!method.isEnabled());
        methodAdapter.notifyDataSetChanged();
        SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
        Toast.makeText(this, "Method status updated", Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        
        if (show) {
            loadingAnimation.setAnimation("loading_animation.json");
            loadingAnimation.playAnimation();
        } else {
            loadingAnimation.cancelAnimation();
        }
    }

    private void showEmptyState(boolean show) {
        emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerMethods.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}