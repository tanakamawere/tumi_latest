package org.tmz.tumi.Main.Account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Objects.BusinessObject;
import org.tmz.tumi.Objects.User;
import org.tmz.tumi.R;
import org.tmz.tumi.StartUp.SplashActivity;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private CircleImageView imageView;
    private TextView userName, businessName;
    private LinearLayout linearLayout;
    private FirebaseMethods firebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new FirstPreference())
                .commit();

        firebaseMethods = new FirebaseMethods();

        imageView = findViewById(R.id.settingProfilePicture);
        userName = findViewById(R.id.usernameSettingsTextView);
        businessName = findViewById(R.id.businessNameSettingsTextView);
        linearLayout = findViewById(R.id.linearLayoutSettings);

        //On Click Listeners
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAccount fragmentAccount = new FragmentAccount();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out)// popExit
                        .addSharedElement(imageView, getString(R.string.transitionProfilePicture))
                        .replace(R.id.frameLayoutSettingsMain, fragmentAccount).addToBackStack(null)
                        .commit();
            }
        });

        //Methods
        try {
            populateAccountInformation();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void populateAccountInformation() {
        DatabaseReference accountInfoDatabase = firebaseMethods.getDatabaseReference();

        //Referring to the user's personal information here
        accountInfoDatabase.child(getString(R.string.fbUserInfo)).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;

                    userName.setText(user.getFullName());
                    UniversalImageLoader.setImage(user.getProfilePicture(), imageView, null, "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Business info
        accountInfoDatabase.child(getString(R.string.fbBusinessInfo)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    BusinessObject business = snapshot.getValue(BusinessObject.class);
                    assert business != null;

                    businessName.setText(business.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static class FirstPreference extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKeyFirst) {
            setPreferencesFromResource(R.xml.first_preference, rootKeyFirst);

            Preference securityFragment = findPreference("securityFragment");
            Preference helpFragment = findPreference("helpFragment");
            Preference shareFragment = findPreference("shareFragment");
            Preference accountFragment = findPreference("accountFragment");
            final Preference theme = findPreference("theme");
            Preference logout = findPreference("logout");

            assert accountFragment != null;
            accountFragment.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    ((SettingsActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.slide_out)// popExit
                            .replace(R.id.settings, new FragAccountSettings()).addToBackStack(null)
                            .commit();
                    return true;
                }
            });

            assert securityFragment != null;
            securityFragment.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    ((SettingsActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.slide_out)// popExit
                            .replace(R.id.settings, new FragmentSecuritySettings()).addToBackStack(null)
                            .commit();
                    return true;
                }
            });

            assert logout != null;
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), SplashActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    requireActivity().finishAffinity();
                    return true;
                }
            });

            assert helpFragment != null;
            helpFragment.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((SettingsActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.slide_out)// popExit
                            .replace(R.id.settings, new HelpFragment()).addToBackStack(null)
                            .commit();
                    return false;
                }
            });

            assert shareFragment != null;
            shareFragment.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ((SettingsActivity) requireContext()).getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,  // enter
                                    R.anim.fade_out,  // exit
                                    R.anim.fade_in,   // popEnter
                                    R.anim.slide_out)// popExit
                            .replace(R.id.settings, new ShareFragment()).addToBackStack(null)
                            .commit();
                    return false;
                }
            });

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor editor = sharedPreferences.edit();
            final boolean isDarkModeOn = sharedPreferences.getBoolean(getString(R.string.isDarkModeOn), false);

            if (isDarkModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            assert theme != null;
            if (isDarkModeOn) {
                theme.setSummary(getString(R.string.darkTheme));
            } else {
                theme.setSummary(getString(R.string.lightTheme));
            }

            theme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (isDarkModeOn) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        editor.putBoolean(getString(R.string.isDarkModeOn), false);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        editor.putBoolean(getString(R.string.isDarkModeOn), true);
                    }
                    editor.apply();
                    return true;
                }
            });
        }
    }

    public static class HelpFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }
    }

    public static class ShareFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }
    }
}