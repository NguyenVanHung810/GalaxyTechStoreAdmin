package com.example.galaxytechstoreadmin;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class AddProductActivity extends AppCompatActivity {

    private EditText title, price, cutted_price, desc;
    private Button save, cancel;
    private ImageView image;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton delete_img, change_img;
    private Uri uri;
    private Dialog loadingDialog;

    int id_cate = 0;
    int id_brand = 0;
    int s = 0;
    String cate;

    ArrayList<String> categoryIdList;
    ArrayList<String> categoryNameList;
    ArrayList<String> brandNameList;
    ArrayList<String> brandIdList;

    private Spinner categorySpinner, brandSpinner;

    @Override
    protected void onStart() {
        super.onStart();
    }

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
                                if(s==0){
                                    s = 1;
                                }
                                else {
                                    categoryIdList.add(documentSnapshot.getId());
                                    categoryNameList.add(documentSnapshot.getString("categoryName"));
                                }
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
                ((TextView) parent.getChildAt(0)).setTextColor(Color.RED);
                ((TextView) parent.getChildAt(0)).setTextSize(18);
                id_cate = position;
                cate = categoryIdList.get(position);
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
                ((TextView) parent.getChildAt(0)).setTextColor(Color.RED);
                ((TextView) parent.getChildAt(0)).setTextSize(18);
                id_brand = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        title.addTextChangedListener(new TextWatcher() {
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

        price.addTextChangedListener(new TextWatcher() {
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

        cutted_price.addTextChangedListener(new TextWatcher() {
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

        desc.addTextChangedListener(new TextWatcher() {
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

        save = (Button) findViewById(R.id.add_btn);
        cancel = (Button) findViewById(R.id.cancel_btn);

        save.setEnabled(false);
        save.setTextColor(Color.argb(50, 255, 255, 255));

        delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = null;
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
                save.setEnabled(false);
                save.setTextColor(Color.argb(50, 255, 255, 255));
                addPic(DBqueries.product_index + 1);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(title.getText())) {
            if (!TextUtils.isEmpty(price.getText())) {
                if (!TextUtils.isEmpty(cutted_price.getText())) {
                    if (!TextUtils.isEmpty(desc.getText())) {
                        save.setEnabled(true);
                        save.setTextColor(Color.rgb(255, 255, 255));
                    } else {
                        save.setEnabled(false);
                        save.setTextColor(Color.argb(50, 255, 255, 255));
                    }
                } else {
                    save.setEnabled(false);
                    save.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                save.setEnabled(false);
                save.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            save.setEnabled(false);
            save.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
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

    private void addPic(Long index) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("product_image/" + "product_image_"+ String.valueOf(index) + ".jpg");
        if(uri != null) {
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
                                            Map<String, Object> product = new HashMap<>();
                                            product.put("index", DBqueries.product_index + 1);
                                            product.put("1_star", 0);
                                            product.put("2_star", 0);
                                            product.put("3_star", 0);
                                            product.put("4_star", 0);
                                            product.put("5_star", 0);
                                            product.put("average_ratings", "0");
                                            product.put("total_ratings", 0);
                                            product.put("COD", true);
                                            product.put("stock_quantity", 100);
                                            product.put("max_quantity", 100);
                                            product.put("in_stock", true);
                                            product.put("offers_applied", 0);
                                            product.put("product_description", desc.getText().toString());
                                            product.put("product_image", uri.toString());
                                            product.put("product_price", price.getText().toString());
                                            product.put("cutted_price", cutted_price.getText().toString());
                                            product.put("product_title", title.getText().toString());

                                            ArrayList<String> tag = new ArrayList<>();
                                            tag.add(categoryNameList.get(id_cate));
                                            tag.add(brandNameList.get(id_brand));
                                            tag.add(title.getText().toString());
                                            tag.add(categoryNameList.get(id_cate).toUpperCase());
                                            tag.add(categoryNameList.get(id_cate).toLowerCase());
                                            tag.add(brandNameList.get(id_brand).toLowerCase());
                                            tag.add(brandNameList.get(id_brand).toUpperCase());
                                            product.put("tags", tag);

                                            product.put("Category_Id", categoryIdList.get(id_cate));
                                            product.put("Brand_Id", brandIdList.get(id_brand));

                                            Map<String, Object> item = new HashMap<>();
                                            item.put("product_image", uri.toString());
                                            item.put("product_title", title.getText().toString());
                                            item.put("product_subtitle", "Hàng chính hãng");
                                            item.put("product_price", price.getText().toString());
                                            addFields(product, item);
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
        else {
            Map<String, Object> product = new HashMap<>();
            product.put("index", DBqueries.product_index + 1);
            product.put("1_star", 0);
            product.put("2_star", 0);
            product.put("3_star", 0);
            product.put("4_star", 0);
            product.put("5_star", 0);
            product.put("average_ratings", "0");
            product.put("total_ratings", 0);
            product.put("COD", true);
            product.put("stock_quantity", 100);
            product.put("max_quantity", 100);
            product.put("in_stock", true);
            product.put("offers_applied", 0);
            product.put("product_description", desc.getText().toString());
            product.put("product_image", "");
            product.put("product_price", price.getText().toString());
            product.put("cutted_price", cutted_price.getText().toString());
            product.put("product_title", title.getText().toString());

            ArrayList<String> tag = new ArrayList<>();
            tag.add(categoryNameList.get(id_cate));
            tag.add(brandNameList.get(id_brand));
            tag.add(title.getText().toString());
            tag.add(categoryNameList.get(id_cate).toUpperCase());
            tag.add(categoryNameList.get(id_cate).toLowerCase());
            tag.add(brandNameList.get(id_brand).toLowerCase());
            tag.add(brandNameList.get(id_brand).toUpperCase());
            product.put("tags", tag);

            product.put("Category_Id", categoryIdList.get(id_cate));
            product.put("Brand_Id", brandIdList.get(id_brand));

            Map<String, Object> item = new HashMap<>();
            item.put("product_image", "");
            item.put("product_title", title.getText().toString());
            item.put("product_subtitle", "Hàng chính hãng");
            item.put("product_price", price.getText().toString());
            addFields(product, item);
        }
    }

    private void addFields(final Map<String, Object> productData, final Map<String, Object> item){
        db.collection("PRODUCTS").add(productData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            db.collection("CATEGORIES").document(categoryIdList.get(id_cate)).collection("BRAND")
                                    .document(brandIdList.get(id_brand))
                                    .collection("ITEMS")
                                    .document(task.getResult().getId()).set(item);

                            db.collection("CATEGORIES").document("HOME").collection("TOP_DEALS")
                                    .document("HCbOkJXjK7jRqkBe77oj")
                                    .collection("ITEMS")
                                    .document(task.getResult().getId())
                                    .set(item);
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}