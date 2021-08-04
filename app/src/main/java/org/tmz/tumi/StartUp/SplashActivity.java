package org.tmz.tumi.StartUp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

import org.tmz.tumi.Main.Dashboard.DashboardActivity;
import org.tmz.tumi.R;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private LauncherManager1 launcherManager1;
    private FirebaseAuth mAuth;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        launcherManager1 = new LauncherManager1(this);

        //Manage splash screen time and appearance
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                } else if (launcherManager1.isFirstTime()) {
                    launcherManager1.setFirstLaunch(false);
                    startActivity(new Intent(SplashActivity.this, NewUserSliderActivity.class));
                } else {
                    try {
                        Fragment mFragment;
                        mFragment = new FragmentSignIn();
                        Bundle bundle2 = new Bundle();
                        bundle2.putString(getString(R.string.fromWhere), getString(R.string.signInFragment));
                        mFragment.setArguments(bundle2);
                        FragmentManager fragmentManager = getSupportFragmentManager();

                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayoutSplash, mFragment)
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        Log.e(TAG, "run: " + e.getMessage());
                    }
                }
            }
        };
        handler.postDelayed(runnable, 1000);

        checkIfDarkModeWasOn();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    private void checkIfDarkModeWasOn() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}