package com.app.aqoong.smsreport.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.app.aqoong.smsreport.R;
import com.app.aqoong.smsreport.data.Globar;
import com.app.aqoong.smsreport.ui.fragment.FindThingsFragment;
import com.app.aqoong.smsreport.ui.fragment.ReportMainFragment;



/**
 * Created by aqoong on 2017. 9. 22..
 */

public class PagerActivity extends AppCompatActivity {

    private PageAdapter pageAdapter = null;
    private ViewPager mViewPager = null;

    private FindThingsFragment findThingsFragment = null;
    private ReportMainFragment reportMainFragment = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //swipe tab view create
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pageAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        findThingsFragment = new FindThingsFragment(this);
        reportMainFragment = new ReportMainFragment(this);
    }

    @Override
    public void onBackPressed() {

        if(findThingsFragment.isStatusWeb()){
            findThingsFragment.getWebView().goBack();
        }
        else
            super.onBackPressed();
    }

    public class PageAdapter extends FragmentStatePagerAdapter {

        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return reportMainFragment;
                case 1:
                    return findThingsFragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return Globar.pageTitle_0;
                case 1:
                    return  Globar.pageTitle_1;
            }

            return null;
        }
    }


}
