package com.sqsong.gankiosample.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sqsong.gankiosample.R;
import com.sqsong.gankiosample.adapter.SimpleFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> mTabTitles = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFragments();
        initEvents();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    private void initFragments() {
        mTabTitles.add(getString(R.string.text_android));
        mTabTitles.add(getString(R.string.text_ios));
        mTabTitles.add(getString(R.string.text_web));

        AndroidFragment androidFragment = new AndroidFragment();
        IOSFragment iosFragment = new IOSFragment();
        WebFragment webFragment = new WebFragment();
        mFragments.add(androidFragment);
        mFragments.add(iosFragment);
        mFragments.add(webFragment);
    }

    private void initEvents() {
        SimpleFragmentAdapter mFragmentAdapter = new SimpleFragmentAdapter(getSupportFragmentManager(), mTabTitles, mFragments);
        viewPager.setAdapter(mFragmentAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
