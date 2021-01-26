package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private static final String TAG = "CrimePagerActivity";
    private List<Crime> mCrimes;
    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            //senza il ciclo for qui sotto, la posizione dell'elemento è 0 di default
            //quindi, senza il ciclo, l'argomento position di getItem vale 0 di default.
            @Override
            public Fragment getItem(int position) {
                Log.d(TAG, "CrimePagerActivity.onCreate.getItem() called");
                Log.d(TAG, "posizione dell'elemento: " + position);
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        //NB: questo ciclo for viene invocato PRIMA dei metodi getItem e getCount dell'adapter
        //di ViewPager
       for (int i = 0; i < mCrimes.size(); i++) {
           Log.d(TAG, "Ciclo per il ritrovamento della posizione dell'elemento");
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }


}
