package com.ibracodeko.layerthirtyfour;

import android.app.Application;
import android.content.Context;
import com.ibracodeko.layerthirtyfour.utils.ThemeManager;
import com.ibracodeko.layerthirtyfour.utils.SoundManager;

public class LayerApplication extends Application {
    private static Context context;
    
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        
        // Initialize theme manager
        ThemeManager.getInstance().initialize(this);
        
        // Initialize sound manager
        SoundManager.getInstance().initialize(this);
    }
    
    public static Context getAppContext() {
        return context;
    }
}