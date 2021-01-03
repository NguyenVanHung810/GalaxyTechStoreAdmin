package com.example.galaxytechstoreadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class UpdateBrandActivity extends AppCompatActivity {

    private EditText brandName;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_brand);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cập nhật thông tin");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        brandName = findViewById(R.id.brand_name);
        save = findViewById(R.id.btn_save);

        brandName.setText(getIntent().getStringExtra("name"));

        brandName.addTextChangedListener(new TextWatcher() {
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setTextColor(Color.argb(50, 255, 255, 255));
                save.setEnabled(false);
                Map<String, Object> brandata = new HashMap<>();
                brandata.put("layout_title", brandName.getText().toString());
                FirebaseFirestore.getInstance().collection("CATEGORIES")
                        .document(getIntent().getStringExtra("cate_id"))
                        .collection("BRAND")
                        .document(getIntent().getStringExtra("id"))
                        .update(brandata)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toasty.success(getApplicationContext(), "Cập nhật thành công !!!", Toasty.LENGTH_SHORT).show();
                                    finish();
                                }
                                else {
                                    Toasty.error(getApplicationContext(), "Cập nhật không thành công !!!", Toasty.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
            }
        });

    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(brandName.getText())) {
            save.setEnabled(true);
            save.setTextColor(Color.rgb(255, 255, 255));
        } else {
            save.setEnabled(false);
            save.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

}