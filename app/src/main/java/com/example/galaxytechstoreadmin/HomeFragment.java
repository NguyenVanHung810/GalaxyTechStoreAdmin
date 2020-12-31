package com.example.galaxytechstoreadmin;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.galaxytechstoreadmin.MainActivity.currentFragment;


public class HomeFragment extends Fragment {

    private ConstraintLayout pm, om, ai;


    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        pm = view.findViewById(R.id.pm_layout);
        om = view.findViewById(R.id.om_layout);
        ai = view.findViewById(R.id.ai_layout);

        pm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                gotoFragment("Quản lý sản phẩm", new ProductFragment(), MainActivity.ProductFragment);
            }
        });

        om.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                gotoFragment("Quản lý đơn hàng", new OrderFragment(), MainActivity.OrdersFragment);
            }
        });

        ai.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                gotoFragment("Thông tin tài khoản", new AccountFragment(), MainActivity.AccountFragment);
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setFragment(Fragment fragment, int FragmentNo) {
        if (FragmentNo != currentFragment) {
            currentFragment = FragmentNo;
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.main_layout, fragment, null);
            ft.setReorderingAllowed(true);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void gotoFragment(String tt, Fragment fragment, int FragmentNo) {
        MainActivity.actionbar_name.setVisibility(View.GONE);
        ((AppCompatActivity) getContext()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getContext()).getSupportActionBar().setTitle(tt);
        getActivity().invalidateOptionsMenu();
        setFragment(fragment, FragmentNo);
    }
}