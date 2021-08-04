package org.tmz.tumi.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.tmz.tumi.Pictures.CameraFragment;
import org.tmz.tumi.Pictures.GalleryFragment;
import org.tmz.tumi.R;

public class PictureSectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "PictureSectionsPagerAdapter";
    private static final int[] TAB_TITLES = new int[]{R.string.tab_picture_1, R.string.tab_picture_2};
    private final Context mContext;

    public PictureSectionsPagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a FragmentSales (defined as a static inner class below).

        Fragment fragmentPagerAdapter = null;
        switch (position) {
            case 0:
                fragmentPagerAdapter = new GalleryFragment();
                break;
            case 1:
                fragmentPagerAdapter = new CameraFragment();
                break;
        }
        assert fragmentPagerAdapter != null;
        return fragmentPagerAdapter;
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

}
