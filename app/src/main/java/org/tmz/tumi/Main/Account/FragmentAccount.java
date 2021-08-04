package org.tmz.tumi.Main.Account;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.CustomDialogs.BottomDialogProfilePictureOptions;
import org.tmz.tumi.Objects.BusinessObject;
import org.tmz.tumi.Objects.User;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.Miscellaneous;
import org.tmz.tumi.Utils.Permissions;
import org.tmz.tumi.Utils.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentAccount extends Fragment {

    public static final int VERIFY_PERMISSIONS = 1;
    private static final String TAG = "FragmentAccount";
    public TextView businessNameTV, userNameTV, userJoinedTV, internetTV, businessType, businessCategory;
    public FirebaseMethods firebaseMethods;
    BusinessObject business;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private CircleImageView profilePicture;
    private ImageButton setProfilePicture;
    private TextView businessAddressTV, businessPhoneTV, businessEmailTV, userEmailTV, userPhoneNumberTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        businessNameTV = view.findViewById(R.id.businessNameTextView);
        businessAddressTV = view.findViewById(R.id.businessAddressTextView);
        businessPhoneTV = view.findViewById(R.id.businessPhoneTextView);
        businessEmailTV = view.findViewById(R.id.businessEmailTextView);
        userEmailTV = view.findViewById(R.id.userEmailTextView);
        userPhoneNumberTV = view.findViewById(R.id.userPhoneNumberTextView);
        businessType = view.findViewById(R.id.businessTypeTextView);
        businessCategory = view.findViewById(R.id.businessCategoryTextView);
        setProfilePicture = view.findViewById(R.id.newProfilePicture);
        profilePicture = view.findViewById(R.id.accountProfilePicture);

        mAuth = FirebaseAuth.getInstance();
        internetTV = view.findViewById(R.id.nullInternetAccountTV);
        internetTV.setVisibility(View.GONE);

        firebaseMethods = new FirebaseMethods();

        //TextViews for the user's information
        userNameTV = view.findViewById(R.id.userFullNameTextView);
        userJoinedTV = view.findViewById(R.id.dateJoinedTextView);

        setProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialogProfilePictureOptions bottom = new BottomDialogProfilePictureOptions();
                bottom.show(requireActivity().getSupportFragmentManager(), null);
            }
        });
        ViewCompat.setTransitionName(profilePicture, getString(R.string.transitionProfilePicture));

        //To share account information externally
        TextView share = view.findViewById(R.id.shareTextView);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareInfoExternally();
            }
        });

        progressBar = view.findViewById(R.id.progressBarAccount);

        if (mAuth.getCurrentUser() != null)
            populateAccountInformation();

        checkForInternetConnection();
        initImageLoader();

        if (checkPermissionsArray(Permissions.PERMISSIONS)) {
            //TODO
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }
    }

    private boolean checkPermissionsArray(String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPermissions(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(requireActivity(), permission);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }

    public void verifyPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(requireActivity(),
                permissions,
                VERIFY_PERMISSIONS);
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(requireActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void checkForInternetConnection() {
        Miscellaneous misc = new Miscellaneous();

        if (!misc.internetAvailable(requireActivity())) {
            internetTV.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void populateAccountInformation() {
        DatabaseReference accountInfoDatabase = firebaseMethods.getDatabaseReference();

        //Referring to the user's personal information here
        accountInfoDatabase.child(getString(R.string.fbUserInfo)).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    progressBar.setVisibility(View.GONE);
                    assert user != null;

                    userNameTV.setText(user.getFullName());
                    userJoinedTV.setText(getString(R.string.joinedOn) + " " + user.getDateJoined());
                    userEmailTV.setText(user.getEmailAddress());
                    userPhoneNumberTV.setText(user.getPhoneNumber());
                    UniversalImageLoader.setImage(user.getProfilePicture(), profilePicture, null, "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Business info
        accountInfoDatabase.child(getString(R.string.fbBusinessInfo)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    business = snapshot.getValue(BusinessObject.class);
                    assert business != null;

                    businessNameTV.setText(business.getName());
                    businessAddressTV.setText(business.getAddress());
                    businessPhoneTV.setText(business.getPhoneNumber());
                    businessEmailTV.setText(business.getEmailAddress());
                    businessType.setText(business.getType());
                    businessCategory.setText(business.getCategory());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void shareInfoExternally() {
        final String businessInfoShared = businessNameTV.getText().toString() + "\n"
                + businessAddressTV.getText().toString() + "\n"
                + businessEmailTV.getText().toString() + "\n"
                + businessPhoneTV.getText().toString();

        final String userInfoShared = userNameTV.getText().toString() + "\n"
                + userPhoneNumberTV.getText().toString() + "\n"
                + userEmailTV.getText().toString();

        CharSequence[] options = new CharSequence[]{
                "Business info", "Personal Info"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent shareBusinessInfo = new Intent(Intent.ACTION_SEND);
                    shareBusinessInfo.putExtra(Intent.EXTRA_TEXT, businessInfoShared);
                    shareBusinessInfo.setType("text/plain");
                    Intent sendBusinessIntent = Intent.createChooser(shareBusinessInfo, null);
                    startActivity(sendBusinessIntent);
                } else if (which == 1) {
                    Intent sharePersonalInfo = new Intent(Intent.ACTION_SEND);
                    sharePersonalInfo.putExtra(Intent.EXTRA_TEXT, userInfoShared);
                    sharePersonalInfo.setType("text/plain");
                    Intent sendPersonalIntent = Intent.createChooser(sharePersonalInfo, null);
                    startActivity(sendPersonalIntent);
                }
            }
        });
        builder.setTitle("Which do you want to share?");
        builder.show();
    }
}
