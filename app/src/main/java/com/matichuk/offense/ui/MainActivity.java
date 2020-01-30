package com.matichuk.offense.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.matichuk.offense.R;
import com.matichuk.offense.ui.fragment.OffenseFragment;
import com.matichuk.offense.ui.fragment.ScanFragment;
import com.matichuk.offense.ui.fragment.SettingFragment;
import com.matichuk.offense.ui.fragment.StatFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.nav_scan);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.nav_offense:
                loadFragment(OffenseFragment.newInstance());
                return true;

            case R.id.nav_scan:
                loadFragment(ScanFragment.newInstance());
                return true;
            case R.id.nav_stat:
                loadFragment(StatFragment.newInstance());
                return true;

            case R.id.nav_setting:
                loadFragment(SettingFragment.newInstance());
                return true;

        }
        return false;
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);
        ft.commit();
    }
}
