package org.tmz.tumi.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tmz.tumi.Main.Account.SettingsActivity;
import org.tmz.tumi.Main.Dashboard.DashboardActivity;

import java.util.HashMap;
import java.util.Objects;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    //Method to initialize parent tree for user.
    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    //Get database
    public DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    //Get user ID
    public String getUserID() {
        return Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    }

    //For the public business and service methods
    public DatabaseReference getBusinessInfoDB() {
        return databaseReference.child("Business Information");
    }

    public DatabaseReference getPublicBusinessDB() {
        return FirebaseDatabase.getInstance().getReference().child("Businesses")
                .child(getUserID());
    }

    public DatabaseReference getServiceBusinessDB() {
        return FirebaseDatabase.getInstance().getReference().child("Services")
                .child(getUserID());
    }

    public void uploadingPhoto(final Context context,
                               String imageURL,
                               Bitmap bitmap,
                               final ProgressDialog dialog,
                               final String location,
                               final double callerActivity,
                               final String key) {
        //TODO
        /*I have to add logic that decides where I want to upload the download URL,
         * either when I am coming from the upload profile picture activity or the advertise product
         * option. */
        dialog.show();

        FilePaths filePaths = new FilePaths();
        final StorageReference storageReference = mStorageReference.child(location);

        //Convert image to bitmap
        if (bitmap == null) {
            /*If the picture is coming from the gallery, we are going to continue as normal
             * However, if the picture was taken by the camera, the bitmap is referred to as one of the
             * method parameters.*/
            bitmap = BitmapConverter.getBitmap(imageURL);
        }

        byte[] bytes = BitmapConverter.getByteFromBitmap(bitmap, 100);

        UploadTask uploadTask = null;
        try {
            uploadTask = storageReference.putBytes(bytes);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "uploadingPhoto: OutOfMemoryError" + e.getMessage());
            Toast.makeText(context, "There has been an error. Please try again", Toast.LENGTH_SHORT).show();
        }
        assert uploadTask != null;
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadURL = "";
                        Log.e(TAG, "onSuccess: The download URL: " + uri);
                        downloadURL = uri.toString();

                        if (callerActivity == 0) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("profilePicture", downloadURL);

                            Log.e(TAG, "updateUserData: Download URL: " + downloadURL);

                            getDatabaseReference().child("User Information").updateChildren(map);
                            getDatabaseReference().child("Business Information").updateChildren(map);

                            Toast.makeText(context, "Picture uploaded successfully.", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, SettingsActivity.class));
                        } else if (callerActivity == 1) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("imageURL", downloadURL);

                            Log.e(TAG, "onSuccess: Advertisement DownloadURL: " + downloadURL);

                            FirebaseDatabase.getInstance().getReference().child("Advertisements")
                                    .child(key).updateChildren(hashMap);
                            getDatabaseReference().child("Explore")
                                    .child("Advertisements").child(key).updateChildren(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                context.startActivity(new Intent(context, DashboardActivity.class));
                                                Toast.makeText(context, "Advertisement successfully uploaded", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Uploading failed. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void changeDatabaseLocation(DatabaseReference fromPath,
                                       final DatabaseReference toPath) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "onComplete: Transferred business information.");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }

}
