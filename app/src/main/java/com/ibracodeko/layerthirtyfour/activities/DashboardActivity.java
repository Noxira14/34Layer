package com.ibracodeko.layerthirtyfour.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.ibracodeko.layerthirtyfour.R;
import com.ibracodeko.layerthirtyfour.models.User;
import com.ibracodeko.layerthirtyfour.utils.PreferenceManager;
import com.ibracodeko.layerthirtyfour.utils.SoundManager;
import com.ibracodeko.layerthirtyfour.utils.ThemeManager;
import com.ibracodeko.layerthirtyfour.utils.NetworkUtils;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView tvWelcome, tvServerStatus;
    private CardView cardWebMultiPorting, cardIPTroubleshooting, cardProxyServer;
    private LottieAnimationView dashboardAnimation;
    private PreferenceManager prefManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        initManagers();
        setupToolbar();
        setupNavigationDrawer();
        setupClickListeners();
        loadUserData();
        startAnimation();
        checkServerStatus();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvServerStatus = findViewById(R.id.tvServerStatus);
        cardWebMultiPorting = findViewById(R.id.cardWebMultiPorting);
        cardIPTroubleshooting = findViewById(R.id.cardIPTroubleshooting);
        cardProxyServer = findViewById(R.id.cardProxyServer);
        dashboardAnimation = findViewById(R.id.dashboardAnimation);
    }

    private void initManagers() {
        prefManager = new PreferenceManager(this);
        currentUser = prefManager.getUser();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("34Layer");
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Update navigation header with user info
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.navUsername);
        TextView navUserType = headerView.findViewById(R.id.navUserType);
        
        if (currentUser != null) {
            navUsername.setText(currentUser.getUsername());
            navUserType.setText(currentUser.getUserType().toString());
        }

        // Show/hide admin menu based on user type
        Menu navMenu = navigationView.getMenu();
        MenuItem adminItem = navMenu.findItem(R.id.nav_admin_panel);
        adminItem.setVisible(currentUser != null && currentUser.getUserType() == User.UserType.ADMIN);
    }

    private void setupClickListeners() {
        cardWebMultiPorting.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            startActivity(new Intent(this, WebMultiPortingActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        cardIPTroubleshooting.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            startActivity(new Intent(this, IPTroubleshootingActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        cardProxyServer.setOnClickListener(v -> {
            SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);
            startActivity(new Intent(this, ProxyServerActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            tvWelcome.setText("Halo " + currentUser.getUsername() + ", Selamat Datang Di 34Layer");
        }
    }

    private void startAnimation() {
        dashboardAnimation.setAnimation("dashboard_animation.json");
        dashboardAnimation.playAnimation();
    }

    private void checkServerStatus() {
        NetworkUtils.checkServerStatus("https://139.59.29.42", new NetworkUtils.ServerStatusCallback() {
            @Override
            public void onResult(boolean isOnline) {
                runOnUiThread(() -> {
                    tvServerStatus.setText(isOnline ? "Server Online" : "Server Offline");
                    tvServerStatus.setTextColor(getResources().getColor(
                        isOnline ? R.color.green_500 : R.color.red_500));
                });
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        SoundManager.getInstance().playSound(SoundManager.SOUND_CLICK);

        if (id == R.id.nav_dashboard) {
            // Already on dashboard
        } else if (id == R.id.nav_web_multiporting) {
            startActivity(new Intent(this, WebMultiPortingActivity.class));
        } else if (id == R.id.nav_ip_troubleshooting) {
            startActivity(new Intent(this, IPTroubleshootingActivity.class));
        } else if (id == R.id.nav_proxy_server) {
            startActivity(new Intent(this, ProxyServerActivity.class));
        } else if (id == R.id.nav_admin_panel) {
            startActivity(new Intent(this, AdminPanelActivity.class));
        } else if (id == R.id.nav_theme_toggle) {
            ThemeManager.getInstance().toggleTheme();
            recreate();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        prefManager.setLoggedIn(false);
        prefManager.clearUser();
        SoundManager.getInstance().playSound(SoundManager.SOUND_SUCCESS);
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}