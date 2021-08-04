package org.tmz.tumi.Utils;

import android.os.Environment;

import com.google.firebase.auth.FirebaseAuth;

public class FilePaths {

    //Paths to access gallery pictures
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/Camera";

    //Path for profile picture
    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
    public String PROFILE_PHOTO_STORAGE_LOCATION = FIREBASE_IMAGE_STORAGE
            + "/"
            + FirebaseAuth.getInstance().getCurrentUser().getUid()
            + "/profile_photo";

    public String ADVERTISEMENT_STORAGE_LOCATION =
            "photos/advertisements";

    public String ONLINE_USER_UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public String RECEIPT_TEMP_IMAGE = ROOT_DIR + "/temporary_file.jpg";
}
