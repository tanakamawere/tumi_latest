package org.tmz.tumi.Finances;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class FragmentRecordExpenses extends Fragment {
    private Spinner appCompatSpinner;
    private TextInputEditText amountTV;
    private DatabaseReference databaseReference;
    private String expenseID = "", type = "";
    private ArrayList<String> arrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appCompatSpinner = view.findViewById(R.id.expenseSpinner);

        amountTV = view.findViewById(R.id.recordExpenseAmountEditText);
        Button confirmBtn = view.findViewById(R.id.recordExpenseBtn);
        Button newExpenseBtn = view.findViewById(R.id.recordNewExpenseTypeBtn);
        //The new material spinner

        //Spinner
        arrayList = new ArrayList<>();

        expenseID = new FirebaseMethods().getDatabaseReference().child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbFinances))
                .child("Expenses")
                .child("User Expenses").push().getKey();

        databaseReference = new FirebaseMethods().getDatabaseReference().child(getString(R.string.fbTumiInfo))
                .child(getString(R.string.fbFinances))
                .child("Expenses");

        newExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentNewExpenseType fragmentNewExpenseType = new FragmentNewExpenseType();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentNewExpenseType.show(fragmentManager, null);
            }
        });

        populateSpinner();
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordExpense();
            }
        });
    }

    private void populateSpinner() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, arrayList);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        databaseReference.child("Expense Types").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String expenseType = "";
                    for (DataSnapshot childSnap : snapshot.getChildren()) {
                        expenseType = Objects.requireNonNull(childSnap.child("type").getValue()).toString();
                        arrayList.add(expenseType);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        appCompatSpinner.setAdapter(adapter);
    }

    private void recordExpense() {
        appCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(requireActivity(), appCompatSpinner.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        type = appCompatSpinner.getSelectedItem().toString();
        String total = amountTV.getText().toString();
        //Date recorded
        Calendar calenderDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        final String currentDate = simpleDateFormat.format(calenderDate.getTime());

        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("total", total);
        map.put("date", currentDate);
        map.put("expenseID", expenseID);
        databaseReference
                .child("User Expenses")
                .child(expenseID).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(requireActivity(), "Uploading information successfully.", Toast.LENGTH_SHORT).show();
                    requireActivity().startActivity(new Intent(requireActivity(), FinancesActivity.class));
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireActivity(), "Failed to upload data. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
