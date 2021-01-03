package com.example.galaxytechstoreadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AddBrandActivity extends AppCompatActivity {

    private EditText brandName;
    private Button save;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_brand);

        position = getIntent().getIntExtra("position", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm thương hiệu mới");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final CategoryModel categoryModel = DBqueries.categoryModelList.get(position);

        brandName = findViewById(R.id.brand_name);
        save = findViewById(R.id.btn_save);

        save.setTextColor(Color.argb(50, 255, 255, 255));
        save.setEnabled(false);

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
                brandata.put("index", categoryModel.getLastBrandIndex() + 1);
                brandata.put("layout_background", "#fff176");
                brandata.put("layout_title", brandName.getText().toString());
                FirebaseFirestore.getInstance().collection("CATEGORIES")
                        .document(categoryModel.getCategoryId()).
                        collection("BRAND")
                        .add(brandata).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toasty.success(getApplicationContext(), "Thêm thành công !!!", Toasty.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toasty.error(getApplicationContext(), "Thêm không thành công !!!", Toasty.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(brandName.getText().toString())) {
            save.setEnabled(true);
            save.setTextColor(Color.rgb(255, 255, 255));
        } else {
            save.setEnabled(false);
            save.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }
}