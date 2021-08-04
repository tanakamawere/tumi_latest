package org.tmz.tumi.Main.Dashboard;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;

import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class FragmentAddDebtCredit extends Fragment {

    private TextInputEditText name, phone, amount, dateET;
    private Button confirm;
    private DatabaseReference databaseReference;
    private String key;
    private Calendar calendar;
    private ProgressDialog progressDialog;
    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_debt_credit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Types for spinner
        String[] MoneyType = new String[]{getString(R.string.fbDebtsType), getString(R.string.fbCreditsType)};
        spinner = view.findViewById(R.id.moneyTypeSpinner);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, MoneyType);
        typesAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(typesAdapter);

        //Widgets Initialization
        name = view.findViewById(R.id.addDCNameEditText);
        phone = view.findViewById(R.id.addDCNumberEditText);
        amount = view.findViewById(R.id.addDCAmountEditText);
        dateET = view.findViewById(R.id.addDCDateEditText);
        confirm = view.findViewById(R.id.addDCButton);

        //Vars
        FirebaseMethods firebaseMethods = new FirebaseMethods();
        databaseReference = firebaseMethods.getDatabaseReference()
                .child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbMoney));
        progressDialog = new ProgressDialog(requireActivity());

        //Random Keys
        key = databaseReference.child(getString(R.string.fbDebts)).push().getKey();

        //Selecting Date from Date Dialog
        calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(requireActivity(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //On Click Listeners
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")
                        || phone.getText().toString().equals("")
                        || amount.getText().toString().equals("")
                        || dateET.getText().toString().equals("")) {
                    Toast.makeText(requireActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle(getString(R.string.pdUploading));
                    progressDialog.setMessage(getString(R.string.pdPleaseWait));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    uploadInformation();
                }
            }
        });
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        dateET.setText(sdf.format(calendar.getTime()));
    }

    private void uploadInformation() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("phone", phone.getText().toString());
        map.put("date", Objects.requireNonNull(dateET.getText()).toString());
        map.put("amount", amount.getText().toString());
        map.put("key", key);
        map.put("type", spinner.getSelectedItem().toString());


        databaseReference.child(key).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireActivity(), "Information successfully uploaded", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(requireActivity(), DashboardActivity.class));
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(), "Information failed to upload", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
