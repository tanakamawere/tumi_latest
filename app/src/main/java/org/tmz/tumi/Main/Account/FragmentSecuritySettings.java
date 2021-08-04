package org.tmz.tumi.Main.Account;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.tmz.tumi.R;
import org.tmz.tumi.StartUp.FragmentSignIn;

public class FragmentSecuritySettings extends PreferenceFragmentCompat {

    private static final String TAG = "FragmentSecurity";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.security_preferences, rootKey);

        Preference editAccountInfo = findPreference("editAccountInfo");
        Preference changePassword = findPreference("changePassword");
        Preference changeEmail = findPreference("changeEmail");
        Preference deleteAccount = findPreference("deleteAccount");

        assert changeEmail != null;
        changeEmail.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentSignIn fragmentSignIn = new FragmentSignIn();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.fromWhere), getString(R.string.settingsFragment));
                bundle.putString(getString(R.string.aboutWhat), getString(R.string.changeEmailAddress));
                fragmentSignIn.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out)
                        .replace(R.id.frameLayoutSettingsMain, fragmentSignIn)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });

        assert changePassword != null;
        changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentSignIn fragmentSignIn = new FragmentSignIn();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.fromWhere), getString(R.string.settingsFragment));
                bundle.putString(getString(R.string.aboutWhat), getString(R.string.changePasswordBundle));
                fragmentSignIn.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out)
                        .replace(R.id.frameLayoutSettingsMain, fragmentSignIn)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });

        assert editAccountInfo != null;
        editAccountInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out)
                        .replace(R.id.frameLayoutSettingsMain, new FragmentEditUserInfo())
                        .addToBackStack(null)
                        .commit();

                return true;
            }
        });

        assert deleteAccount != null;
        deleteAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentSignIn fragment = new FragmentSignIn();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.fromWhere), getString(R.string.settingsFragment));
                bundle.putString(getString(R.string.aboutWhat), getString(R.string.deleteAccount));
                fragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out)
                        .replace(R.id.frameLayoutSettingsMain, fragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
        });
    }
}
