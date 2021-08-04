package org.tmz.tumi.StartUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import org.tmz.tumi.Adapters.Adapter;
import org.tmz.tumi.R;

public class NewUserSliderActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private Button nextBtn;
    private int[] layout;
    ViewPager.OnPageChangeListener viewPagerChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == layout.length - 1) {
                nextBtn.setText("Create an account");
            } else {
                nextBtn.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_slider);

        viewPager = findViewById(R.id.viewPager);
        nextBtn = findViewById(R.id.viewPagerButton);

        layout = new int[]{
                R.layout.new_user_slider_1,
                R.layout.new_user_slider_2,
                R.layout.new_user_slider_3
        };

        adapter = new Adapter(this, layout);
        viewPager.setAdapter(adapter);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() + 1 < layout.length) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                } else {
                    startActivity(new Intent(NewUserSliderActivity.this, SplashActivity.class));
                    finish();
                }
            }
        });

        viewPager.addOnPageChangeListener(viewPagerChangeListener);
    }
}