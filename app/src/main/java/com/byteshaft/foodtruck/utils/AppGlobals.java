package com.byteshaft.foodtruck.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

public class AppGlobals extends Application {

    private static Context sContext;
    public static Typeface typefaceBold;
    public static Typeface typefaceNormal;

    public static final String BASE_URL = "http://178.62.35.70/api/";

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        typefaceBold = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/bold.ttf");
        typefaceNormal = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/normal.ttf");
    }

    public static Context getContext() {
        return sContext;
    }
}
