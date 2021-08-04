package org.tmz.tumi.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.tmz.tumi.Main.Explore.FragmentExplore;
import org.tmz.tumi.Main.Explore.FragmentRequests;
import org.tmz.tumi.Main.Explore.FragmentServices;
import org.tmz.tumi.R;

public class ExplorePagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "YouPagerAdapter";
    private static final int[] TAB_TITLES = new int[]{R.string.products, R.string.requests, R.string.services};
    private final Context mContext;

    public ExplorePagerAdapter(Context context, @NonNull FragmentManager fm) {
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
                fragmentPagerAdapter = new FragmentExplore();
                break;
            case 1:
                fragmentPagerAdapter = new FragmentRequests();
                break;
            case 2:
                fragmentPagerAdapter = new FragmentServices();
                break;
        }
        assert fragmentPagerAdapter != null;
        return fragmentPagerAdapter;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

}
