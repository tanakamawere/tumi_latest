package org.tmz.tumi.CustomDialogs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.StorageReference;

import org.tmz.tumi.Pictures.PictureActivity;
import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FilePaths;
import org.tmz.tumi.Utils.FirebaseMethods;

import java.util.HashMap;

public class BottomDialogProfilePictureOptions extends BottomSheetDialogFragment {

    private static final String TAG = "BottomDialogProfilePict";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_dialog_profile_picture_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton remove = view.findViewById(R.id.removePicture);
        ImageButton gallery = view.findViewById(R.id.openGalleryBottomSheet);

        final ProgressDialog progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setMessage(getString(R.string.pdUploading));
        progressDialog.setTitle(getString(R.string.pdPleaseWait));
        progressDialog.setCanceledOnTouchOutside(false);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMethods firebaseMethods = new FirebaseMethods();
                HashMap<String, Object> map = new HashMap<>();
                map.put("profilePicture", null);

                firebaseMethods.getDatabaseReference().child(getString(R.string.fbTumiInfo)).updateChildren(map);
                firebaseMethods.getDatabaseReference().child(getString(R.string.fbBusinessInfo)).updateChildren(map);

                StorageReference storageReference = firebaseMethods.mStorageReference.child(new FilePaths().PROFILE_PHOTO_STORAGE_LOCATION);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "onSuccess: Picture deleted.");
                        Toast.makeText(requireActivity(), "Picture removed successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), PictureActivity.class);
                intent.putExtra(getString(R.string.comingFromWhere), getString(R.string.comingFromAccount));
                startActivity(intent);
                dismiss();
            }
        });
    }
}
