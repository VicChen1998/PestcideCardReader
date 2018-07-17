package com.vicchen.pestcidecardreader;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.vicchen.pestcidecardreader.Analysis.Analysis;
import com.vicchen.pestcidecardreader.History.History;
import com.vicchen.pestcidecardreader.Settings.Settings;


public class MainActivity extends AppCompatActivity {

    private final int pageCount = 3;

    private ViewPager viewPager;
    private BottomNavigationView navigation;

    private Analysis analysis;
    private History history;
    private Settings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 初始化三个页面
        analysis = new Analysis();
        history = new History();
        settings = new Settings();


        // 配置viewPager
        viewPager = findViewById(R.id.viewPager);
        // 设置预加载及保留fragment数 防止自动销毁
        viewPager.setOffscreenPageLimit(pageCount);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });


        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return analysis;
                    case 1:
                        return history;
                    case 2:
                        return settings;
                }
                return null;
            }

            @Override
            public int getCount() {
                return pageCount;
            }
        });


        // 配置navigation
        navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                viewPager.setCurrentItem(menuItem.getOrder());
                return true;
            }
        });


    }


}
