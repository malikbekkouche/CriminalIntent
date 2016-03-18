package com.example.malik.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by malik on 3/2/16.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
