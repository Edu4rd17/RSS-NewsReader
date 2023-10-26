package com.example.assignmentrss;

/**
 * @author Eduard Iacob
 */

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        openSubscribeFragment();
    }

    // This method is used to open subscribe fragment
    public void openSubscribeFragment() {
        Fragment fragment = new SubscribeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }
}
