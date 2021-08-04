package org.tmz.tumi.Pictures;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.tmz.tumi.Adapters.PictureSectionsPagerAdapter;
import org.tmz.tumi.R;

public class PictureActivity extends AppCompatActivity {


    private static final String TAG = "PictureActivity";
    private TabLayout tabs;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        tabs = findViewById(R.id.tabLayoutPicture);
        viewPager = findViewById(R.id.viewPagerPicture);

        setUpViewPager();
    }

    public void setUpViewPager() {

        PictureSectionsPagerAdapter sectionsPagerAdapter = new PictureSectionsPagerAdapter(PictureActivity.this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }
}