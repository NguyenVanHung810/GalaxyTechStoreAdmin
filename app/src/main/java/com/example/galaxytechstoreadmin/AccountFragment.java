package com.example.galaxytechstoreadmin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private Button signOutBtn, changeProfile;
    private TextView name, email;
    private LinearLayout layoutContainer;
    private Dialog loadingDialog;

    public AccountFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        // init
        name=root.findViewById(R.id.user_name);
        email=root.findViewById(R.id.user_email);
        layoutContainer=root.findViewById(R.id.layout_container);
        signOutBtn=root.findViewById(R.id.sign_out);
        changeProfile = root.findViewById(R.id.change_profile_btn);
        // init

        ////////// Loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(root.getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //////////loading dialog

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                DBqueries.clearData();
                DBqueries.email=null;  //my code
                startActivity(new Intent(getContext(), Login_Register_ResetPassword_Activity.class));
                getActivity().finish();
            }
        });

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updateUserInfo = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfo.putExtra("Name",name.getText());
                updateUserInfo.putExtra("Email",email.getText());
                updateUserInfo.putExtra("Phone", DBqueries.phone);
                startActivity(updateUserInfo);
            }
        });

        loadingDialog.dismiss();

        return root;
    }
}