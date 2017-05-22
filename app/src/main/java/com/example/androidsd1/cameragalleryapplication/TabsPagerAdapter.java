package com.example.androidsd1.cameragalleryapplication;

/**
 * Created by Android SD-1 on 26-04-2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CameraFragment.newInstance();
            case 1:
                return GalleryFragment.newInstance();
        }
        return GalleryFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "CAMERA";
            case 1:
                return "GALLERY";
        }
        return "";
    }
}