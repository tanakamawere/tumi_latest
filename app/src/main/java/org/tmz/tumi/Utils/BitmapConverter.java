package org.tmz.tumi.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitmapConverter {
    private static final String TAG = "BitmapConverter";

    public static Bitmap getBitmap(String imgURL) {
        File imageFile = new File(imgURL);
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;

        try {
            fileInputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "getBitmap: " + e);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "getBitmap: OutOfMemoryError" + e);
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "getBitmap: " + e);
            }
        }
        return bitmap;
    }

    //Converting Bitmap to Bytes
    public static byte[] getByteFromBitmap(Bitmap bm, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
}
