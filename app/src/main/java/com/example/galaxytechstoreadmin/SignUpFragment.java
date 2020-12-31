package com.example.galaxytechstoreadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpFragment extends Fragment {

    private TextView AlreadyHaveAcc;
    private ProgressBar load;
    private EditText email, fullname, phonenumber, password, cfpassword;
    private Button signup;

    public static boolean diableCloseBtn = false;


    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        AlreadyHaveAcc = (TextView) view.findViewById(R.id.tv_have_account);
        load = (ProgressBar) view.findViewById(R.id.load);
        email = (EditText) view.findViewById(R.id.sign_in_email);
        fullname = (EditText) view.findViewById(R.id.sign_in_name);
        phonenumber = (EditText) view.findViewById(R.id.sign_in_phone);
        password = (EditText) view.findViewById(R.id.sign_up_password);
        cfpassword = (EditText) view.findViewById(R.id.sign_up_confirm);
        signup = (Button) view.findViewById(R.id.btn_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AlreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cfpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });
    }

    private void checkEmailAndPassword() {
        if (email.getText().toString().matches(emailPattern)) {
            if (password.getText().toString().equals(cfpassword.getText().toString())) {
                load.setVisibility(View.VISIBLE);
                signup.setEnabled(false);
                signup.setTextColor(Color.argb(50,255,255,255));
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<Object, String> userData = new HashMap<>();
                            userData.put("name", fullname.getText().toString());
                            userData.put("phonenumber", phonenumber.getText().toString());
                            userData.put("email", email.getText().toString());

                            firestore.collection("ADMINS").document(firebaseAuth.getUid())
                                    .set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(getActivity(), MainActivity.class));
                                                getActivity().finish();
                                            } else {
                                                String error = task.getException().getMessage();
                                                toastInfo(error);
                                            }
                                        }
                                    });
                        } else {
                            signup.setEnabled(true);
                            signup.setTextColor(Color.rgb(255, 255, 255));
                            load.setVisibility(View.INVISIBLE);
                            String error = task.getException().getMessage();
                            toastInfo(error);
                        }
                    }
                });
            } else {
                cfpassword.setError("Password doesn't matched !!!", getResources().getDrawable(R.mipmap.error_ic));
            }
        } else {
            email.setError("Invalid Email", getResources().getDrawable(R.mipmap.error_ic));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_left);
        ft.replace(R.id.login_register_resetpassword_layout,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void checkInput() {
        if(!TextUtils.isEmpty(email.getText().toString())){
            if(!TextUtils.isEmpty(fullname.getText().toString())){
                if(!TextUtils.isEmpty(password.getText().toString()) && password.length() >= 8){
                    if(!TextUtils.isEmpty(cfpassword.getText().toString())){
                        signup.setEnabled(true);
                    }
                    else {
                        signup.setEnabled(false);
                    }
                }
                else {
                    signup.setEnabled(false);
                }
            }
            else {
                signup.setEnabled(false);
            }
        }
        else {
            signup.setEnabled(false);
        }
    }

    private void toastInfo(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
    }

    private void mainIntent(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        diableCloseBtn = false;
        getActivity().finish();
    }
}