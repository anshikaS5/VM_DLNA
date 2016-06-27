package com.vmokshagroup.dlnaplayer;

import android.app.Application;
import android.content.Context;

import com.vmokshagroup.dlnaplayer.util.FontsOverride;

/**
 * Created by anshikas on 20-01-2016.
 */
public class ApplicationVM_DLNA extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        setCustomFonts();
    }

    /**
     * Replaces application default font with custom font.
     */
    private void setCustomFonts() {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Exo2-Medium.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "Exo2-Regular.ttf");
        FontsOverride.setDefaultFont(this, "DEFAULT", "Exo2-Medium.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "Exo2-SemiBold.ttf");
    }

}

