package com.example.galaxytechstoreadmin;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AddProductActivity extends AppCompatActivity {

    private EditText title, price, cutted_price, desc, s_quantity, m_quantity;
    private Button save, cancel;
    private ImageView image;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton delete_img, change_img;
    private Uri uri;
    private Boolean updatePhoto = false;
    private Dialog loadingDialog;
    int id_cate = 0;
    int id_brand = 0;
    long nop = 0;
    long next=0;
    String cate;

    ArrayList<String> categoryIdList;
    ArrayList<String> categoryNameList;
    ArrayList<String> brandNameList;
    ArrayList<String> brandIdList;

    private Spinner categorySpinner, brandSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thêm mới sản phẩm");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //////////loading dialog
        loadingDialog = new Dialog(getApplicationContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //////////loading dialog


        title = (EditText) findViewById(R.id.product_title);
        price = (EditText) findViewById(R.id.product_price);
        cutted_price = (EditText) findViewById(R.id.product_cutted_price);
        desc = (EditText) findViewById(R.id.description);
        s_quantity = (EditText) findViewById(R.id.stock_quantity);
        m_quantity = (EditText) findViewById(R.id.max_quantity);
        image = (ImageView) findViewById(R.id.product_image);
        delete_img = (ImageButton) findViewById(R.id.delete_img_btn);
        change_img = (ImageButton) findViewById(R.id.change_img_btn);
        categorySpinner = (Spinner) findViewById(R.id.spinner_category);
        brandSpinner = (Spinner) findViewById(R.id.spinner_brand);

        categoryIdList = new ArrayList<String>();
        categoryNameList = new ArrayList<String>();
        brandNameList = new ArrayList<>();
        brandIdList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, brandNameList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(adapter1);

        FirebaseFirestore.getInstance().collection("CATEGORIES")
                .orderBy("index")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryIdList.add(documentSnapshot.getId());
                                categoryNameList.add(documentSnapshot.getString("categoryName"));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().toString();
                            Toasty.error(getApplicationContext(), error, Toasty.LENGTH_SHORT).show();
                        }
                    }
                });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_cate = position;
                cate = categoryIdList.get(position);
                Toasty.error(AddProductActivity.this, cate, Toasty.LENGTH_SHORT).show();
                brandNameList.clear();
                brandIdList.clear();
                FirebaseFirestore.getInstance().collection("CATEGORIES").document(cate).collection("BRAND")
                        .orderBy("index")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    brandIdList.add(documentSnapshot.getId());
                                    brandNameList.add(documentSnapshot.get("layout_title").toString());
                                }
                                adapter1.notifyDataSetChanged();
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_brand = position;
                Toasty.error(AddProductActivity.this, parent.getItemAtPosition(position).toString(), Toasty.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        save = (Button) findViewById(R.id.add_btn);
        cancel = (Button) findViewById(R.id.cancel_btn);

        delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getApplicationContext()).load(R.drawable.no_img).into(image);
            }
        });

        change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, 1);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    }
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 1);
                }
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> product = new HashMap<>();

                // số thứ tự sản phẩm
                product.put("index", DBqueries.index + 1);

                // Do mới tạo ra sản phẩm sẽ chưa có ai đánh giá nên các giá trị liên quan đến việc rating đều đưa về bằng 0.
                // số lượng 1 sao
                product.put("1_star", 0);
                // số lượng 2 sao
                product.put("2_star", 0);
                // số lượng 3 sao
                product.put("3_star", 0);
                // số lượng 4 sao
                product.put("4_star", 0);
                // số lượng 5 sao
                product.put("5_star", 0);
                // Trung bình các sao
                product.put("average_ratings", "0");
                // Tổng số lượng các sao
                product.put("total_ratings", 0);

                product.put("COD", true);

                // số lượng ban đầu tạo
                product.put("stock_quantity", Integer.parseInt(s_quantity.getText().toString()));
                // số lượng tồn
                product.put("max_quantity", Integer.parseInt(m_quantity.getText().toString()));
                product.put("in_stock", true);
                // số lượng offer đưa vào một sản phẩm
                product.put("offers_applied", 0);
                // mô tả
                product.put("product_description", desc.getText().toString());
                // hình ảnh
                product.put("product_image", uri.toString());
                // giá gốc sản phẩm
                product.put("product_price", price.getText().toString());
                // giá đã giảm so với giá gốc
                product.put("cutted_price", cutted_price.getText().toString());
                // tiêu đề, tên sản phẩm
                product.put("product_title", title.getText().toString());

                // các tags dùng để tìm kiếm sản phẩm
                ArrayList<String> tag = new ArrayList<>();
                tag.add("dien thoai");
                tag.add("realme");
                tag.add("laptop");
                tag.add("tablet");
                product.put("tags", tag);

                FirebaseFirestore.getInstance().collection("CATEGORIES").document(categoryIdList.get(id_cate)).collection("BRAND")
                        .document(brandIdList.get(id_brand)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            nop = (long) task.getResult().get("no_of_products");
                            for (long x = 1; x <= nop; x++) {
                                next = x;
                            }
                            next = next + 1;
                        }
                    }
                });

                db.collection("PRODUCTS").add(product)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {

                                    updatePic(task.getResult().getId());
                                    Map<String, Object> category = new HashMap<>();
                                    category.put("no_of_products", nop + 1);
                                    category.put("product_id_" + next, task.getResult().getId());
                                    category.put("product_image_" + next, uri.toString());
                                    category.put("product_title_" + next, title.getText().toString());
                                    category.put("product_subtitle_" + next, desc.getText().toString());
                                    category.put("product_price_" + next, price.getText().toString());

                                    db.collection("CATEGORIES").document(categoryIdList.get(id_cate)).collection("BRAND")
                                            .document(brandIdList.get(id_brand)).update(category);

                                    DBqueries.productModelList.clear();
                                    ProductFragment.productAdapter.notifyDataSetChanged();
                                    Toasty.success(getApplicationContext(), "Thêm mới sản phẩm thành công !", Toast.LENGTH_SHORT, true).show();
                                    finish();
                                } else {
                                    Toasty.error(getApplicationContext(), "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    updatePhoto = true;
                    Glide.with(getApplicationContext()).load(uri).into(image);
                } else {
                    Toasty.error(getApplicationContext(), "Image not found!", Toast.LENGTH_SHORT, true).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            } else {
                Toasty.error(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT, true).show();
            }
        }
    }

    private void updatePic(String id) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("pi/" + id + ".jpg");
        Glide.with(getApplicationContext()).asBitmap().load(uri).centerCrop().into(new ImageViewTarget<Bitmap>(image) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = storageReference.putBytes(data);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        uri = task.getResult();
                                    } else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return;
            }

            @Override
            protected void setResource(@Nullable Bitmap resource) {
                image.setImageResource(R.drawable.no_img);
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}