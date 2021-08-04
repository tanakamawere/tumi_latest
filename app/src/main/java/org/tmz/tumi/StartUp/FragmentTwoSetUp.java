package org.tmz.tumi.StartUp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.database.DatabaseReference;

import org.tmz.tumi.Main.Dashboard.DashboardActivity;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

public class FragmentTwoSetUp extends Fragment {

    private static final String TAG = "FragmentTwoSetUp";

    private Spinner typeSpinner, categorySpinner;
    private DatabaseReference databaseReference;
    private String[] businessTypes, serviceCategory, resellingCategory;
    private ArrayAdapter<String> typesAdapter, categoryAdapter;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two_business_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        businessTypes = new String[]{"Service Business", "Reselling Business"};

        serviceCategory = new String[]{"Catering",
                "Cleaning",
                "Hairdressing",
                "Barber",
                "Plumbing",
                "Landscaping",
                "Rentals",
                "Legal Services",
                "Photography & Media",
                "Printing",
                "Other"};

        resellingCategory = new String[]{
                "Tech Accessories",
                "Food & Drink",
                "Clothing",
                "Medicine",
                "General Products",
                "Books",
                "Beauty Products",
                "Cleaning Supplies",
                "Other"
        };

        //This is for the first spinner
        typesAdapter = new ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, businessTypes);
        typesAdapter.setDropDownViewResource(R.layout.spinner_item);
        typeSpinner = view.findViewById(R.id.primaryTypeSpinner);
        typeSpinner.setAdapter(typesAdapter);

        categorySpinner = view.findViewById(R.id.primaryCategorySpinner);
        button = view.findViewById(R.id.saveBusinessTwoInfoButton);

        databaseReference = new FirebaseMethods().getDatabaseReference().child(getString(R.string.fbBusinessInfo));
        initializeFirstSpinner();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadBusinessInfo();
            }
        });
    }

    private void initializeFirstSpinner() {
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (typeSpinner.getSelectedItemPosition() == 0) {
                    initializeSecondSpinner(serviceCategory);
                } else if (typeSpinner.getSelectedItemPosition() == 1) {
                    initializeSecondSpinner(resellingCategory);
                }
                Log.e(TAG, "onItemSelected: " + typeSpinner.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(requireActivity(), "Please select an option", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initializeSecondSpinner(String[] category) {
        categoryAdapter = new ArrayAdapter<String>(requireActivity(), R.layout.spinner_item, category);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void uploadBusinessInfo() {
        startActivity(new Intent(requireActivity(), DashboardActivity.class));
        Toast.makeText(getActivity(), "Welcome.", Toast.LENGTH_SHORT).show();
        getActivity().finishAffinity();
        /*HashMap<String, Object> map = new HashMap<>();
        map.put("Type", typeSpinner.getSelectedItem().toString());
        map.put("Category", categorySpinner.getSelectedItem().toString());
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(requireActivity(), DashboardActivity.class));
                    Toast.makeText(getActivity(), "Welcome.", Toast.LENGTH_SHORT).show();
                    getActivity().finishAffinity();
                } else {
                    Toast.makeText(getActivity(), "Failed to upload user info successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
}
