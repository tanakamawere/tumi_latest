package org.tmz.tumi.StartUp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tmz.tumi.Main.Dashboard.DashboardActivity;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;

public class FragmentSignIn extends Fragment {

    private static final String TAG = "FragmentSignIn";
    private FirebaseAuth mAuth;
    private TextInputEditText mEmail, mPassword;
    private Button mSignIn;
    private DatabaseReference rootDB;
    private Bundle bundle, bundle2;
    private String selector, exactSelector;
    private LinearLayout linearLayout;
    private TextView selectorTextView, deleteDataNotification, forgotPasswordPrompter;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSignIn = view.findViewById(R.id.signInButton);
        Button mCreateAccountFragment = view.findViewById(R.id.signInCreateAccountButton);
        mCreateAccountFragment.setOnClickListener(v -> {
        });

        mAuth = FirebaseAuth.getInstance();
        mEmail = view.findViewById(R.id.enterAppEmail);
        mPassword = view.findViewById(R.id.enterAppPassword);
        selectorTextView = view.findViewById(R.id.signInSelector);
        linearLayout = view.findViewById(R.id.linearLayoutSignUp);
        dialog = new ProgressDialog(requireActivity());
        deleteDataNotification = view.findViewById(R.id.deleteDataNotification);
        deleteDataNotification.setVisibility(View.GONE);
        rootDB = FirebaseDatabase.getInstance().getReference();
        forgotPasswordPrompter = view.findViewById(R.id.forgotPasswordPrompterSignIn);

        //Setting up the dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(getString(R.string.pdPleaseWait));
        dialog.setMessage("Working...");

        //Receiving the bundle
        bundle = getArguments();
        assert bundle != null;
        selector = bundle.get(getString(R.string.fromWhere)).toString();
        try {
            exactSelector = bundle.get(getString(R.string.aboutWhat)).toString();
        } catch (Exception e) {
            Log.e(TAG, "onViewCreated: " + e.toString());
        }

        if (selector.equals(getString(R.string.signInFragment))) {
            mSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(requireActivity(), DashboardActivity.class));
                    Toast.makeText(getActivity(), "Welcome.", Toast.LENGTH_SHORT).show();
                    getActivity().finishAffinity();
                }
            });
        } else if (selector.equals(getString(R.string.settingsFragment))) {
            selectorTextView.setText("Re-authenticate");
            linearLayout.setVisibility(View.GONE);
            if (exactSelector.equals(getString(R.string.changeEmailAddress)) || exactSelector.equals(getString(R.string.changePasswordBundle))) {
                deleteDataNotification.setVisibility(View.GONE);
            } else {
                deleteDataNotification.setVisibility(View.VISIBLE);
            }
            forgotPasswordPrompter.setVisibility(View.GONE);
            mSignIn.setText(getString(R.string.button_string_confirm));
            mSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    reAuthenticateUser();
                }
            });
        }

        mCreateAccountFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mFragment;
                mFragment = new FragmentCreateAccount();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.frameLayoutSplash, mFragment)
                        .addToBackStack(null).commit();
            }
        });

        forgotPasswordPrompter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mFragment;
                mFragment = new FragmentForgotPassword();

                Bundle bundle2 = new Bundle();
                bundle2.putString(getString(R.string.fromWhere), getString(R.string.signInFragment));
                mFragment.setArguments(bundle2);

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.frameLayoutSplash, mFragment)
                        .addToBackStack(null).commit();
            }
        });
    }

    private void signIn() {
        final String Memail = mEmail.getText().toString();
        final String Mpassword = mPassword.getText().toString();

        if (Memail.isEmpty()) {
            Toast.makeText(requireActivity(), "Enter a valid email address.", Toast.LENGTH_SHORT).show();
        } else if (Mpassword.isEmpty()) {
            Toast.makeText(requireActivity(), "Enter a valid password.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                Fragment mFragment;
                mFragment = new FragmentOneSetUp();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.frameLayoutSplash, mFragment)
                        .addToBackStack(null).commit();
                /*mAuth.signInWithEmailAndPassword(Memail, Mpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Fragment mFragment;
                            mFragment = new FragmentOneSetUp();
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                            fragmentManager.beginTransaction().replace(R.id.frameLayoutSplash, mFragment)
                                    .addToBackStack(null).commit();
                        } else {
                            Toast.makeText(requireActivity(), "Authentication details are wrong.\nPlease try again.", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });*/
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "I hope you catch the error");
            }
        }
    }

    private void reAuthenticateUser() {
        final FirebaseMethods firebaseMethods = new FirebaseMethods();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(mEmail.getText().toString(), mPassword.getText().toString());

        // Prompt the user to re-provide their sign-in credentials
        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "User re-authenticated.");

                            if (exactSelector.equals(getString(R.string.deleteAccount))) {
                                //Checking for not deleted ads and deleting if not found.
                                firebaseMethods.getDatabaseReference().child(getString(R.string.fbExplore)).child(getString(R.string.fbAdvertisements))
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Toast.makeText(requireActivity(), "First delete publicized products and requests", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    //removing user node
                                                    firebaseMethods.getDatabaseReference().removeValue();

                                                    //removing public business node
                                                    rootDB.child(getString(R.string.fbBusinesses)).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                rootDB.child(getString(R.string.fbBusinesses)).child(user.getUid()).removeValue();
                                                            } else {
                                                                Log.e(TAG, "onDataChange: No business node found");
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    user.delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(requireActivity(), "Account deleted", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                            } else if (exactSelector.equals(getString(R.string.changeEmailAddress))) {
                                FragmentForgotPassword fragmentForgotPassword = new FragmentForgotPassword();
                                Bundle bundle = new Bundle();
                                bundle.putString(getString(R.string.fromWhere), getString(R.string.changeEmailAddress));
                                fragmentForgotPassword.setArguments(bundle);

                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(
                                                R.anim.slide_in,  // enter
                                                R.anim.fade_out,  // exit
                                                R.anim.fade_in,   // popEnter
                                                R.anim.slide_out)
                                        .replace(R.id.frameLayoutSettingsMain, fragmentForgotPassword)
                                        .addToBackStack(null)
                                        .commit();
                            } else if (exactSelector.equals(getString(R.string.changePasswordBundle))) {
                                FragmentForgotPassword fragmentForgotPassword = new FragmentForgotPassword();
                                Bundle bundle = new Bundle();
                                bundle.putString(getString(R.string.fromWhere), getString(R.string.settingsFragment));
                                fragmentForgotPassword.setArguments(bundle);

                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .setCustomAnimations(
                                                R.anim.slide_in,  // enter
                                                R.anim.fade_out,  // exit
                                                R.anim.fade_in,   // popEnter
                                                R.anim.slide_out)
                                        .replace(R.id.frameLayoutSettingsMain, fragmentForgotPassword)
                                        .addToBackStack(null)
                                        .commit();
                            }
                            dialog.dismiss();
                        }
                    }
                });
    }
}