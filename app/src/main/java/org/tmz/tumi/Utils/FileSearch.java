package org.tmz.tumi.Utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {
    private static final String TAG = "FileSearch";

    public static ArrayList<String> getDirectoryPaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFile = file.listFiles();
        assert listFile != null;
        try {
            for (File value : listFile) {
                if (value.isDirectory())
                    pathArray.add(value.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.e(TAG, "getDirectoryPaths: " + e.toString());
        }
        return pathArray;
    }

    //getting the files
    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listFile = file.listFiles();
        assert listFile != null;
        for (File value : listFile) {
            if (value.isFile())
                pathArray.add(value.getAbsolutePath());
        }
        return pathArray;
    }
}
