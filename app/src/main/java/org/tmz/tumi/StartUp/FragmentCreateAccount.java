package org.tmz.tumi.StartUp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import org.tmz.tumi.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class FragmentCreateAccount extends Fragment {

    private static final String TAG = "FragmentCreateAccount";

    private TextInputEditText fEmail, fPassword, fFullName;
    private TextView passwordInfo;
    private FirebaseAuth mAuth;
    private TextInputLayout passwordLayout;
    private CountryCodePicker codeNumPicker;
    private String phone1, fullName, email, password, UserID, userPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button mSignInDirector = view.findViewById(R.id.singUpSignInButton);
        mSignInDirector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mFragment;
                mFragment = new FragmentSignIn();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                Bundle bundle2 = new Bundle();
                bundle2.putString(getString(R.string.fromWhere), getString(R.string.signInFragment));
                mFragment.setArguments(bundle2);

                fragmentManager.beginTransaction().replace(R.id.frameLayoutSplash, mFragment)
                        .addToBackStack(null).commit();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        fEmail = view.findViewById(R.id.signUpEmailEditText);
        fPassword = view.findViewById(R.id.signUpPasswordEditText);
        fFullName = view.findViewById(R.id.signUpNameEditText);
        passwordInfo = view.findViewById(R.id.passwordWarningTextViewCreateAccount);
        passwordInfo.setVisibility(View.GONE);

        final EditText phoneEditText = view.findViewById(R.id.signUpPhoneNumberEditText);

        Button mCreateAccount = view.findViewById(R.id.createAccountBtn);
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Country Code picker
                codeNumPicker = view.findViewById(R.id.countryCodeHolder2);
                codeNumPicker.registerCarrierNumberEditText(phoneEditText);
                phone1 = codeNumPicker.getFullNumberWithPlus();
                Log.e(TAG, "onClick: Number: " + phone1);

                createAccount();
            }
        });

        //Password typing process
        passwordLayout = view.findViewById(R.id.signUpPasswordEditTextLayout);
        passwordLayout.setHelperTextEnabled(true);
        passwordLayout.setHelperText("Minimum characters for password: 8");
        fPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 8) {
                    passwordLayout.setHelperText("Password too short");
                } else if (s.toString().length() >= 8) {
                    passwordLayout.setHelperText("Great password");
                }
            }
        });
    }

    private void createAccount() {
        email = fEmail.getText().toString();
        password = fPassword.getText().toString();
        fullName = fFullName.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a valid email address.", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a valid password.", Toast.LENGTH_SHORT).show();
        } else if (fullName.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a valid name.", Toast.LENGTH_SHORT).show();
        } else if (phone1.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a valid phone number..", Toast.LENGTH_SHORT).show();
        } else {
            if (password.length() < 6) {
                passwordInfo.setVisibility(View.VISIBLE);
            } else {
                try {
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Creating Account");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((requireActivity()), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Fragment mFragment;
                                mFragment = new FragmentSignIn();
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                                fragmentManager.beginTransaction().replace(R.id.frameLayoutSplash, mFragment)
                                        .addToBackStack(null).commit();

                                Toast.makeText(requireActivity(), "Account created successfully.\nYou can now log in.", Toast.LENGTH_LONG).show();
                                createUserTree();
                            } else {
                                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Creating account failed."
                                        , Snackbar.LENGTH_LONG).setAction("More Info", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Fragment fragment2 = new FragmentCreateAccountError();
                                        requireActivity().getSupportFragmentManager()
                                                .beginTransaction().replace(R.id.frameLayoutSplash, fragment2)
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                }).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createUserTree() {
        fullName = fFullName.getText().toString();
        email = fEmail.getText().toString();
        UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userPosition = "Position eg Owner";

        final DatabaseReference rootReference;
        rootReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);

        //Date joined.
        Calendar calenderDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yyyy", Locale.getDefault());
        final String currentDate = simpleDateFormat.format(calenderDate.getTime());

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", fullName);
        userMap.put("phoneNumber", phone1);
        userMap.put("emailAddress", email);
        userMap.put("dateJoined", currentDate);
        rootReference.child(getString(R.string.fbUserInfo)).updateChildren(userMap);
    }
}
