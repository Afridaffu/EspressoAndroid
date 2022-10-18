package com.coyni.mapp.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.coyni.mapp.fragments.addnewaccounts.NewBusinessInformationFragment;
import com.coyni.mapp.fragments.addnewaccounts.NewPersonalInformationFragment;

public class AddNewAccountsAdapter extends FragmentPagerAdapter {
    int count = 0;

    public AddNewAccountsAdapter(FragmentManager fm, int count) {
        super(fm);
        this.count = count;
    }


    @Override
    public Fragment getItem(int position) {
        // Return a SlideFragment (defined as a static inner class below).
        if (count == 1 && position == 0) {
            return new NewBusinessInformationFragment();
        } else if (position == 0) {
            return new NewPersonalInformationFragment();
        } else {
            return new NewBusinessInformationFragment();
        }
    }

    @Override
    public int getCount() {
        return count;
    }
}

