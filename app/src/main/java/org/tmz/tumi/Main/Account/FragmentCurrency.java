package org.tmz.tumi.Main.Account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.HashMap;

public class FragmentCurrency extends Fragment {

    private static final String TAG = "FragmentCurrency";

    private FirebaseMethods firebaseMethods;
    private TextView rand, zwl, tumiDefault, exchangeHouseTextView;
    private TextInputEditText randET, zwlET, tumiDefaultET;
    private DatabaseReference databaseReference, exchangeRateDB;
    private String selector, spinnerSelection, exchangeHouse;
    private SharedPreferences sharedPreferences;
    private LinearLayout linearLayoutViewER, linearLayoutSetER;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_currency, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initializing the layout views
        linearLayoutViewER = view.findViewById(R.id.linearLayoutCurrencyViewER);
        linearLayoutSetER = view.findViewById(R.id.linearLayoutCurrencySetER);

        //Determining whether user wants to view exchange rates or set them.
        Bundle bundle = getArguments();
        selector = bundle.getString(getString(R.string.fromWhere));

        if (selector.equals(getString(R.string.fragmentViewER))) {
            linearLayoutViewER.setVisibility(View.VISIBLE);
            linearLayoutSetER.setVisibility(View.GONE);
        } else if (selector.equals(getString(R.string.fragmentSetER))) {
            linearLayoutSetER.setVisibility(View.VISIBLE);
            linearLayoutViewER.setVisibility(View.GONE);
            try {
                getTumiExchangeRate();
            } catch (NullPointerException nullPointerException) {
                Toast.makeText(requireActivity(), "You haven't saved any values online", Toast.LENGTH_SHORT).show();
            }
        }
        //Shared Preferences initialization
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        exchangeHouse = sharedPreferences.getString(getString(R.string.exchangeRate), getString(R.string.interBankExchangeRateValue));
        Log.e(TAG, "onViewCreated: Exchange rate house: " + exchangeHouse);

        //Views for Viewing the ER
        spinner = view.findViewById(R.id.whichRateSpinner);

        rand = view.findViewById(R.id.currencyRandTextView);
        zwl = view.findViewById(R.id.currencyZWLTextView);
        tumiDefault = view.findViewById(R.id.currencyTumiDefaultTextView);
        exchangeHouseTextView = view.findViewById(R.id.fragmentCurrencyExchangeHouseTextView);

        //Views for setting the exchange rate
        randET = view.findViewById(R.id.randSetEREditText);
        zwlET = view.findViewById(R.id.zwlSetEREditText);
        tumiDefaultET = view.findViewById(R.id.tumiDefaultSetEREditText);
        Button confirmSetER = view.findViewById(R.id.confirmSetER);
        Button resetSetER = view.findViewById(R.id.resetSetCurrencies);

        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.fbCurrency));
        firebaseMethods = new FirebaseMethods();
        exchangeRateDB = firebaseMethods.getDatabaseReference().child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbFinances)).child(getString(R.string.fbCurrency));

        //Methods and On Clicks
        confirmSetER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setER();
            }
        });
        resetSetER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    exchangeRateDB.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(requireActivity(), SettingsActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireActivity(), "Failed to reset your rates", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NullPointerException nullPointerException) {
                    Log.e(TAG, "onClick: " + nullPointerException.toString());
                }
            }
        });
        spinnerMethod();
    }

    private void spinnerMethod() {
        //Spinner to set user setRates
        String[] whichRate = {getString(R.string.tumiProvidedRate), getString(R.string.userProvidedRate)};

        spinnerArrayAdapter = new ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, whichRate);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(0);
        spinnerSelection = spinner.getSelectedItem().toString();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals(getString(R.string.tumiProvidedRate))) {
                    getTumiExchangeRate();
                } else if (parent.getItemAtPosition(position).toString().equals(getString(R.string.userProvidedRate))) {
                    getUserSetRate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getUserSetRate() {
        exchangeRateDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    rand.setText(snapshot.child(getString(R.string.fbRand)).getValue().toString());
                    zwl.setText(snapshot.child(getString(R.string.fbZwl)).getValue().toString());
                    tumiDefault.setText(snapshot.child(getString(R.string.fbTumiDefault)).getValue().toString());
                } else {
                    Toast.makeText(requireActivity(), "You haven't enabled Exchange Rate Editing", Toast.LENGTH_SHORT).show();
                    spinnerMethod();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setER() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(getString(R.string.fbRand), randET.getText().toString());
        map.put(getString(R.string.fbZwl), zwlET.getText().toString());
        map.put(getString(R.string.fbTumiDefault), tumiDefaultET.getText().toString());

        exchangeRateDB.child(getString(R.string.fbCurrency)).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(requireActivity(), "Rates uploaded successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireActivity(), SettingsActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(), "Error: Please try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTumiExchangeRate() {
        if (exchangeHouse.equals(getString(R.string.interBankExchangeRateValue))) {
            exchangeHouseTextView.setText(getString(R.string.interBankExchangeRateLabel));
            databaseReference.child(getString(R.string.fbInterbank)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (selector.equals(getString(R.string.fragmentViewER))) {
                            rand.setText(snapshot.child(getString(R.string.fbRand)).getValue().toString());
                            zwl.setText(snapshot.child(getString(R.string.fbZwl)).getValue().toString());
                            tumiDefault.setText(snapshot.child(getString(R.string.fbTumiDefault)).getValue().toString());
                        } else if (selector.equals(getString(R.string.fragmentViewER))) {
                            randET.setText(snapshot.child(getString(R.string.fbRand)).getValue().toString());
                            zwlET.setText(snapshot.child(getString(R.string.fbZwl)).getValue().toString());
                            tumiDefaultET.setText(snapshot.child(getString(R.string.fbTumiDefault)).getValue().toString());
                        }
                    } else {
                        Log.e(TAG, "onDataChange: Some error happened");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else if (exchangeHouse.equals(getString(R.string.blackMarketExchangeRateValue))) {
            exchangeHouseTextView.setText(getString(R.string.blackMarketExchangeRateLabel));
            databaseReference.child(getString(R.string.fbBlackMarket)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (selector.equals(getString(R.string.fragmentViewER))) {
                            rand.setText(snapshot.child(getString(R.string.fbRand)).getValue().toString());
                            zwl.setText(snapshot.child(getString(R.string.fbZwl)).getValue().toString());
                            tumiDefault.setText(snapshot.child(getString(R.string.fbTumiDefault)).getValue().toString());
                        } else if (selector.equals(getString(R.string.fragmentViewER))) {
                            randET.setText(snapshot.child(getString(R.string.fbRand)).getValue().toString());
                            zwlET.setText(snapshot.child(getString(R.string.fbZwl)).getValue().toString());
                            tumiDefaultET.setText(snapshot.child(getString(R.string.fbTumiDefault)).getValue().toString());
                        }
                    } else {
                        Log.e(TAG, "onDataChange: Some error happened");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}
