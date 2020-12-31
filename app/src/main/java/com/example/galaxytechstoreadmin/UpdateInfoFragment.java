package com.example.galaxytechstoreadmin;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class UpdateInfoFragment extends Fragment {

    private String pattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+", user_name, user_email, user_phone;
    private EditText email, name, password, phone;
    private Button updateUserInfoBtn, doneBtn;
    private Dialog loadingDialog, passwordDialog;

    public UpdateInfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_info, container, false);
        name = view.findViewById(R.id.name_et);
        email = view.findViewById(R.id.email_et);
        phone = view.findViewById(R.id.sdt);
        updateUserInfoBtn = view.findViewById(R.id.update_info_button);

        //////////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //////////loading dialog

        //////////password dialog
        passwordDialog = new Dialog(getContext());
        passwordDialog.setContentView(R.layout.password_confirmation_dialog);
        passwordDialog.setCancelable(true);
        passwordDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        passwordDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        password = passwordDialog.findViewById(R.id.enter_password);
        doneBtn = passwordDialog.findViewById(R.id.done_btn);
        //////////password dialog

        user_name = getArguments().getString("Name");
        user_email = getArguments().getString("Email");
        user_phone = getArguments().getString("Phone");

        name.setText(user_name);
        email.setText(user_email);
        phone.setText(user_phone);


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        updateUserInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmail();
            }
        });

        return view;
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(email.getText())) {
            if (!TextUtils.isEmpty(name.getText())) {
                updateUserInfoBtn.setEnabled(true);
                updateUserInfoBtn.setTextColor(Color.rgb(255, 255, 255));
            } else {
                updateUserInfoBtn.setEnabled(false);
                updateUserInfoBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            updateUserInfoBtn.setEnabled(false);
            updateUserInfoBtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    private void checkEmail() {
        if (email.getText().toString().matches(pattern)) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (email.getText().toString().toLowerCase().trim().equals(user_email.toLowerCase().trim())) {
                loadingDialog.show();
                Map<String, Object> userdata = new HashMap<>();
                userdata.put("name", name.getText().toString());
                userdata.put("email", email.getText().toString());
                userdata.put("phonenumber", phone.getText().toString());
                updateFields(user, userdata);
            } else {  //update email
                passwordDialog.show();
                doneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userPass = password.getText().toString();
                        passwordDialog.dismiss();
                        loadingDialog.show();
                        AuthCredential credential = EmailAuthProvider.getCredential(user_email, userPass);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updateEmail(email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Map<String, Object> userdata = new HashMap<>();
                                                userdata.put("name", name.getText().toString());
                                                userdata.put("email", email.getText().toString());
                                                userdata.put("phonenumber", phone.getText().toString());
                                                updateFields(user, userdata);
                                            } else {
                                                loadingDialog.dismiss();
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        } else {
            email.setError("Invalid Email!");
        }
    }

    private void updateFields(FirebaseUser user, final Map<String, Object> userdata){
        FirebaseFirestore.getInstance().collection("ADMINS").document(user.getUid())
                .update(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(userdata.size() >2){
                        DBqueries.email=email.getText().toString().trim();
                        DBqueries.fullname=name.getText().toString().trim();
                    }else {
                        DBqueries.fullname=name.getText().toString().trim();
                        DBqueries.email=email.getText().toString();
                    }
                    getActivity().finish();
                    Toasty.success(getContext(), "Đã cập nhật thành công !!!", Toast.LENGTH_SHORT, true).show();
                }else {
                    String error=task.getException().getMessage();
                    Toasty.error(getContext(), error,Toasty.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }
}