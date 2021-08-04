package org.tmz.tumi.StartUp;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.tmz.tumi.Main.Account.SettingsActivity;
import org.tmz.tumi.R;

public class FragmentForgotPassword extends Fragment {

    private static final String TAG = "FragmentForgotPassword";

    private TextInputEditText email, passwordOne, passwordTwo;
    private TextInputLayout emailTL, passwordOneTL, passwordTwoTL;
    private Bundle bundle;
    private String selector;
    private TextView mainInfo, secondaryInfo;
    private ProgressDialog dialog;
    private Button confirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initializing
        email = view.findViewById(R.id.forgotPasswordEmailAddress);
        passwordOne = view.findViewById(R.id.forgotPasswordStep1);
        passwordTwo = view.findViewById(R.id.forgotPasswordStep2);
        mainInfo = view.findViewById(R.id.passWordInfoTextView);
        secondaryInfo = view.findViewById(R.id.secondaryInfoTextView);
        emailTL = view.findViewById(R.id.forgotPasswordEmailAddressTextLayout);
        passwordOneTL = view.findViewById(R.id.forgotPasswordStep1TextLayout);
        passwordTwoTL = view.findViewById(R.id.forgotPasswordStep2TextLayout);
        confirm = view.findViewById(R.id.forgotPasswordButton);

        //Setting up progress dialog
        dialog = new ProgressDialog(requireActivity());
        dialog.setTitle(getString(R.string.pdPleaseWait));
        dialog.setMessage("Working...");
        dialog.setCanceledOnTouchOutside(false);

        //Receiving bundle
        bundle = getArguments();
        selector = bundle.get(getString(R.string.fromWhere)).toString();

        //If user wants to reset password
        if (selector.equals(getString(R.string.signInFragment))) {
            passwordOneTL.setVisibility(View.GONE);
            passwordTwoTL.setVisibility(View.GONE);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    if (email.getText().toString().equals("")) {
                        Toast.makeText(requireActivity(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        sendResetLink();
                    }
                }
            });
        } //If user wants to change password
        else if (selector.equals(getString(R.string.settingsFragment))) {
            emailTL.setVisibility(GONE);
            mainInfo.setText(R.string.changePassword);
            secondaryInfo.setVisibility(View.GONE);

            confirm.setText(getString(R.string.button_string_confirm));
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    if (passwordTwo.getText().toString().equals("")) {
                        Toast.makeText(requireActivity(), "Enter a valid password", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        changePassword();
                    }
                }
            });
        } //If user wants to change email address
        else if (selector.equals(getString(R.string.changeEmailAddress))) {
            passwordOneTL.setVisibility(View.GONE);
            passwordTwoTL.setVisibility(View.GONE);
            mainInfo.setText(R.string.changeEmail);
            secondaryInfo.setText("This will be your new sign in email address");

            confirm.setText(getString(R.string.button_string_confirm));
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    if (email.getText().toString().equals("")) {
                        Toast.makeText(requireActivity(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        changeEmailAddress();
                    }
                }
            });
        }

        //If password is minimum length
        passwordOneTL.setHelperTextEnabled(true);
        passwordOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < 8) {
                    passwordOneTL.setHelperText("Password too short");
                    passwordOneTL.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
                } else if (s.toString().length() >= 8) {
                    passwordOneTL.setHelperText("Great password");
                    passwordOneTL.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
                }
            }
        });

        //Seeing if the passwords match
        passwordTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(passwordOne.getText().toString())) {
                    passwordTwoTL.setErrorEnabled(true);
                    passwordTwoTL.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));
                    passwordTwoTL.setError("Password do not match");
                } else {
                    passwordTwoTL.setHelperTextEnabled(true);
                    passwordTwoTL.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
                    passwordTwoTL.setHelperText("Passwords match");
                }
            }
        });
    }

    private void changeEmailAddress() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        user.updateEmail(email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Email address updated", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "User email address updated.");
                        } else {
                            Toast.makeText(requireActivity(), "Failed to change email address", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    private void sendResetLink() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String emailAddress = email.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Link to change password has been sent to "
                                    + emailAddress, Toast.LENGTH_LONG).show();

                            if (bundle.get(getString(R.string.fromWhere)).toString().equals(getString(R.string.signInFragment))) {
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(
                                                R.anim.slide_in,  // enter
                                                R.anim.fade_out,  // exit
                                                R.anim.fade_in,   // popEnter
                                                R.anim.slide_out)
                                        .replace(R.id.frameLayoutSplash, new FragmentSignIn())
                                        .addToBackStack(null)
                                        .commit();
                            } else if (bundle.get(getString(R.string.fromWhere)).toString().equals(getString(R.string.settingsFragment))) {
                                startActivity(new Intent(requireActivity(), SettingsActivity.class));
                            }
                        } else {
                            Toast.makeText(requireActivity(), "Failed to send reset link", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });
    }

    private void changePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = passwordTwo.getText().toString();

        assert user != null;
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity(), "Password updated", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "User password updated.");
                        } else {
                            Toast.makeText(requireActivity(), "Failed to change password", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });
    }
}
