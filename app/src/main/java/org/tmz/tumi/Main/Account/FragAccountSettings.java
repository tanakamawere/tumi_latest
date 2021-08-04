package org.tmz.tumi.Main.Account;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

public class FragAccountSettings extends PreferenceFragmentCompat {

    private static final String TAG = "FragAccountSettings";
    private FirebaseMethods firebaseMethods;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.account_preferences, rootKey);

        firebaseMethods = new FirebaseMethods();
        final SwitchPreference publicBusiness = findPreference(getString(R.string.enablePublicBusiness));
        final Preference viewER = findPreference(getString(R.string.viewExchangeRates));
        final Preference setER = findPreference(getString(R.string.setExchangeRates));
        final ListPreference businessType = findPreference(getString(R.string.businessTypeSelection));

        assert businessType != null;
        businessType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (businessType.getValue().equals(getString(R.string.bothValue))) {
                    firebaseMethods.changeDatabaseLocation(firebaseMethods.getBusinessInfoDB(),
                            firebaseMethods.getPublicBusinessDB());
                    firebaseMethods.changeDatabaseLocation(firebaseMethods.getBusinessInfoDB(),
                            firebaseMethods.getServiceBusinessDB());
                } else if (businessType.getValue().equals(getString(R.string.retailValue))) {
                    firebaseMethods.changeDatabaseLocation(firebaseMethods.getBusinessInfoDB(),
                            firebaseMethods.getPublicBusinessDB());
                    //Deleting from services child
                    firebaseMethods.getServiceBusinessDB().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                firebaseMethods.getServiceBusinessDB().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                            } else {
                                Log.e(TAG, "onDataChange: Deleted");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                } else if (businessType.getValue().equals(getString(R.string.serviceValue))) {
                    firebaseMethods.changeDatabaseLocation(firebaseMethods.getBusinessInfoDB(),
                            firebaseMethods.getServiceBusinessDB());
                    //Deleting from services child
                    firebaseMethods.getPublicBusinessDB().addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                firebaseMethods.getPublicBusinessDB().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                            } else {
                                Log.e(TAG, "onDataChange: Deleted public business");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                return true;
            }
        });

        assert publicBusiness != null;
        publicBusiness.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.e(TAG, "onPreferenceChange: Preference: " + preference.getKey() + " is now " + newValue.toString());
                if ((Boolean) newValue) {
                    firebaseMethods.changeDatabaseLocation(firebaseMethods.getBusinessInfoDB(),
                            firebaseMethods.getPublicBusinessDB());
                    Toast.makeText(requireActivity(), "Business is now public", Toast.LENGTH_SHORT).show();
                    publicBusiness.setSwitchTextOn("Public");
                } else {
                    firebaseMethods.getPublicBusinessDB().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(requireActivity(), "Business made private", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireActivity(), "There was error in privatising business", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                publicBusiness.setChecked((Boolean) newValue);
                return true;
            }
        });

        assert viewER != null;
        viewER.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentCurrency fragmentCurrency = new FragmentCurrency();
                Bundle bundle1 = new Bundle();
                bundle1.putString(getString(R.string.fromWhere), getString(R.string.fragmentViewER));
                fragmentCurrency.setArguments(bundle1);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out)// popExit
                        .replace(R.id.frameLayoutSettingsMain, fragmentCurrency).addToBackStack(null)
                        .commit();
                return true;
            }
        });

        assert setER != null;
        setER.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentCurrency fragmentCurrency = new FragmentCurrency();
                Bundle bundle2 = new Bundle();
                bundle2.putString(getString(R.string.fromWhere), getString(R.string.fragmentSetER));
                fragmentCurrency.setArguments(bundle2);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out)// popExit
                        .replace(R.id.frameLayoutSettingsMain, fragmentCurrency).addToBackStack(null)
                        .commit();
                return true;
            }
        });
    }
}
