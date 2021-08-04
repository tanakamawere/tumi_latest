package org.tmz.tumi.Pictures;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.tmz.tumi.R;
import org.tmz.tumi.Utils.FilePaths;
import org.tmz.tumi.Utils.FirebaseMethods;
import org.tmz.tumi.Utils.UniversalImageLoader;

public class UploadPictureActivity extends AppCompatActivity {

    private static final String TAG = "UploadProfilePictureAct";
    public String mAppend = "file:/";
    public String imageURL;
    public Bitmap bitmapURL;
    private ImageView imageView;
    private Button upload;
    private ProgressDialog dialog;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        upload = findViewById(R.id.uploadDPButton);
        imageView = findViewById(R.id.uploadProfilePicture);

        dialog = new ProgressDialog(UploadPictureActivity.this);

        db = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("User Information");

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Uploading");
                dialog.setMessage("Please wait...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
        });
        getIncomingIntent();
        setPicture();
        initImageLoader();
    }

    private void getIncomingIntent() {
        Intent intent = getIntent();
        FirebaseMethods firebaseMethods = new FirebaseMethods();
        if (intent.hasExtra(getString(R.string.selectedImage))) {

            imageURL = intent.getStringExtra(getString(R.string.selectedImage));
            firebaseMethods.uploadingPhoto(UploadPictureActivity.this,
                    imageURL,
                    null,
                    dialog,
                    new FilePaths().PROFILE_PHOTO_STORAGE_LOCATION,
                    0,
                    null);
            Log.e(TAG, "onCreate: " + intent.getStringExtra(getString(R.string.selectedImage)));
        } else if (intent.hasExtra(getString(R.string.selectedBitmap))) {
            ProgressDialog progressDialogCamera = new ProgressDialog(UploadPictureActivity.this);
            progressDialogCamera.setTitle("Please wait");
            progressDialogCamera.setMessage("Uploading picture...");
            progressDialogCamera.setCanceledOnTouchOutside(false);
            progressDialogCamera.show();

            bitmapURL = intent.getParcelableExtra(getString(R.string.selectedBitmap));
            firebaseMethods.uploadingPhoto(UploadPictureActivity.this,
                    null,
                    bitmapURL, progressDialogCamera,
                    new FilePaths().PROFILE_PHOTO_STORAGE_LOCATION,
                    0, null);
        }
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(UploadPictureActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    private void setPicture() {
        Log.e(TAG, "setPicture: ");
        UniversalImageLoader.setImage(imageURL, imageView, null, mAppend);
    }
}