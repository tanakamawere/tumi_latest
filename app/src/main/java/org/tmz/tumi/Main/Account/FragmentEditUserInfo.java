package org.tmz.tumi.Main.Account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.HashMap;
import java.util.Objects;

public class FragmentEditUserInfo extends Fragment {

    TextInputEditText userEditName, userEditPhone,
            businessEditName, businessEditAddress,
            businessEditPhoneNumber, businessDescription, businessType, businessCategory;
    private DatabaseReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userEditName = view.findViewById(R.id.editUserName);
        userEditPhone = view.findViewById(R.id.editUserPhoneNumber);

        businessEditName = view.findViewById(R.id.editBusinessName);
        businessEditAddress = view.findViewById(R.id.editBUsinessAddress);
        businessEditPhoneNumber = view.findViewById(R.id.editBusinessPhoneNumber);
        businessDescription = view.findViewById(R.id.editBusinessDescription);
        businessCategory = view.findViewById(R.id.editBusinessCategory);
        businessType = view.findViewById(R.id.editBusinessType);

        reference = new FirebaseMethods().getDatabaseReference();

        Button confirmEdits = view.findViewById(R.id.confirmEditsBtn);
        confirmEdits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEdits();
            }
        });
        displayAlreadySavedData();
    }

    private void displayAlreadySavedData() {
        //Displaying user's personal information in the editTexts
        reference.child(getString(R.string.fbUserInfo)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userEditName.setText(Objects.requireNonNull(snapshot.child("fullName").getValue()).toString());
                    userEditPhone.setText(Objects.requireNonNull(snapshot.child("phoneNumber").getValue()).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Displaying user's business information in the editTexts
        reference.child(getString(R.string.fbBusinessInfo))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        businessEditName.setText(Objects.requireNonNull(snapshot.child("Name").getValue()).toString());
                        businessEditAddress.setText(Objects.requireNonNull(snapshot.child("Address").getValue()).toString());
                        businessEditPhoneNumber.setText(Objects.requireNonNull(snapshot.child("PhoneNumber").getValue()).toString());
                        businessDescription.setText(snapshot.child(getString(R.string.description)).getValue().toString());
                        businessType.setText(snapshot.child(getString(R.string.type)).getValue().toString());
                        businessCategory.setText(snapshot.child(getString(R.string.category)).getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateEdits() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.pdUploading));
        progressDialog.setMessage(getString(R.string.pdPleaseWait));
        progressDialog.setCanceledOnTouchOutside(false);

        String userName = userEditName.getText().toString();
        String userPhone = userEditPhone.getText().toString();

        String businessName = businessEditName.getText().toString();
        String businessAddress = businessEditAddress.getText().toString();
        String businessPhoneNumber = businessEditPhoneNumber.getText().toString();

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", userName);
        userMap.put("phoneNumber", userPhone);

        HashMap<String, Object> businessMap = new HashMap<>();
        businessMap.put("Name", businessName);
        businessMap.put("Address", businessAddress);
        businessMap.put("PhoneNumber", businessPhoneNumber);
        businessMap.put(getString(R.string.description), businessDescription.getText().toString());
        businessMap.put(getString(R.string.category), businessCategory.getText().toString());
        businessMap.put(getString(R.string.type), businessType.getText().toString());


        if (businessName.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a valid business name.", Toast.LENGTH_SHORT).show();
        } else if (businessAddress.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a valid address.", Toast.LENGTH_SHORT).show();
        } else if (businessPhoneNumber.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a valid phone number.", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            //Assigning the first hashMap to the user info node
            reference.child(getString(R.string.fbBusinessInfo))
                    .updateChildren(businessMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Information saved.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                                startActivity(intent);
                                requireActivity().finish();
                            } else {
                                Toast.makeText(getActivity(), "Information upload failed. Check your internet connection.", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });

            //assigning the second hashMap
            reference.child(getString(R.string.fbUserInfo)).updateChildren(userMap);
        }
    }
}
