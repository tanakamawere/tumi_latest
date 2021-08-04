package org.tmz.tumi.StartUp;

import android.content.Context;
import android.content.SharedPreferences;

public class LauncherManager1 {
    private static final String PrefName = "LauncherManager";
    private static final String isFirstTime = "isFirst";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public LauncherManager1(Context context) {
        sharedPreferences = context.getSharedPreferences(PrefName, 0);
        editor = sharedPreferences.edit();
    }

    public void setFirstLaunch(boolean isFirst) {
        editor.putBoolean(isFirstTime, isFirst);
        editor.commit();
    }

    public boolean isFirstTime() {
        return sharedPreferences.getBoolean(isFirstTime, true);
    }
}
