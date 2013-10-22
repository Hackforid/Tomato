package com.smilehacker.tomato.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.smilehacker.tomato.R;
import com.smilehacker.tomato.fragment.ConfigFragment;
import com.smilehacker.tomato.fragment.TimerFragment;

public class MainActivity extends ActionBarActivity {
    private final static String TAG = "MainActivity";

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPager();
    }

    private void initPager() {
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.main_pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return new TimerFragment();
                }
                case 1: {
                    return new ConfigFragment();
                }

                default: break;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
    
}
