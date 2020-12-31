package com.example.galaxytechstoreadmin;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.FrameLayout;

public class UpdateUserInfoActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private FrameLayout frameLayout;
    private UpdateInfoFragment updateInfoFragment;
    private UpdatePasswordFragment updatePasswordFragment;
    private String name, email, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);
    }
}