package com.greenbox.coyni.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.greenbox.coyni.fragments.onboarding.FragmentOne;
import com.greenbox.coyni.fragments.onboarding.FragmentThree;
import com.greenbox.coyni.fragments.onboarding.FragmentTwo;


public class AutoScrollPagerAdapter extends FragmentPagerAdapter {
  public AutoScrollPagerAdapter(FragmentManager fm) {
    super(fm);
  }
  @Override
  public Fragment getItem(int position) {
    // Return a SlideFragment (defined as a static inner class below).
    if(position == 0){
      return new FragmentOne();
    }else if(position == 1){
      return new FragmentTwo();
    }else{
      return new FragmentThree();
    }
  }
  @Override
  public int getCount() {
    // Show 3 total pages.
    return 3;
  }
}