package com.example.galaxytechstoreadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.pchmn.androidverify.App.getContext;

public class UpdateProductActivity extends AppCompatActivity {

    private EditText title, price, cutted_price, desc;
    private Button save, cancel;
    private ImageView image;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageButton delete_img, change_img;

    private Spinner cateSpinner;
    private Spinner brandSpinner;

    private Boolean updatePhoto = false;
    private Uri uri;

    private ProductModel productModel;

    int id_cate = 0;
    int id_brand = 0;
    String cate;
    int s = 0;

    int position_cate;
    int position_brand;

    ArrayList<String> categoryIdList;
    ArrayList<String> categoryNameList;
    ArrayList<String> brandNameList;
    ArrayList<String> brandIdList;

    private Dialog loadingDialog;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cập nhật thông tin sản phẩm");
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

        Intent intent = getIntent();

        title = (EditText) findViewById(R.id.product_title);
        price = (EditText) findViewById(R.id.product_price);
        cutted_price = (EditText) findViewById(R.id.product_cutted_price);
        desc = (EditText) findViewById(R.id.description);
        image = (ImageView) findViewById(R.id.product_image);
        delete_img = (ImageButton) findViewById(R.id.delete_img_btn);
        change_img = (ImageButton) findViewById(R.id.change_img_btn);
        cateSpinner = (Spinner) findViewById(R.id.spinner_category);
        brandSpinner = (Spinner) findViewById(R.id.spinner_brand);

        productModel = DBqueries.productModelList.get(intent.getIntExtra("position", -1));

        title.setText(productModel.getProductTitle());
        price.setText(productModel.getProductPrice());
        cutted_price.setText(productModel.getCuttedPrice());
        desc.setText(productModel.getProductDesc());
        if(!productModel.getProductImage().equals("")){
            Glide.with(getApplicationContext()).load(productModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.no_img)).into(image);
        }

        categoryIdList = new ArrayList<String>();
        categoryNameList = new ArrayList<String>();
        brandNameList = new ArrayList<>();
        brandIdList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cateSpinner.setAdapter(adapter);

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

        cateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getChildAt(position_cate);
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


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setEnabled(false);
                save.setTextColor(Color.argb(50, 255, 255, 255));
                updatePic(productModel.getIndex(), categoryIdList.get(id_cate), brandIdList.get(id_brand));
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = null;
                updatePhoto = true;
                Glide.with(getApplicationContext()).load(R.drawable.no_img).into(image);
            }
        });

        change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, 1);

                    }else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
                    }
                }else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 1);
                }
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

        if(requestCode == 2){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }else {
                Toasty.error(getApplicationContext(),"Permission denied",Toast.LENGTH_SHORT,true).show();
            }
        }
    }

    private void updatePic(long index, String cate_id, String brand_id) {
        if (updatePhoto) {
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

                                                Map<String, Object> productData = new HashMap<>();
                                                productData.put("product_price", price.getText().toString());
                                                productData.put("product_title", title.getText().toString());
                                                productData.put("product_description", desc.getText().toString());
                                                productData.put("cutted_price", cutted_price.getText().toString());
                                                productData.put("product_image", uri.toString());
                                                productData.put("Category_Id", categoryIdList.get(id_cate));
                                                productData.put("Brand_Id", brandIdList.get(id_brand));
                                                ArrayList<String> tag = new ArrayList<>();
                                                tag.add(categoryNameList.get(id_cate));
                                                tag.add(brandNameList.get(id_brand));
                                                tag.add(title.getText().toString());
                                                tag.add(categoryNameList.get(id_cate).toUpperCase());
                                                tag.add(categoryNameList.get(id_cate).toLowerCase());
                                                tag.add(brandNameList.get(id_brand).toLowerCase());
                                                tag.add(brandNameList.get(id_brand).toUpperCase());
                                                productData.put("tags", tag);

                                                Map<String, Object> home_item = new HashMap<>();
                                                home_item.put("product_image", uri.toString());
                                                home_item.put("product_title", title.getText().toString());
                                                home_item.put("product_price", price.getText().toString());

                                                Map<String, Object> cate_item = new HashMap<>();
                                                cate_item.put("product_image", uri.toString());
                                                cate_item.put("product_subtitle", "Hàng chính hãng");
                                                cate_item.put("product_title", title.getText().toString());
                                                cate_item.put("product_price", price.getText().toString());
                                                updateFields(cate_id, brand_id, productData, home_item, cate_item);
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
                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> productData = new HashMap<>();
                            productData.put("product_price", price.getText().toString());
                            productData.put("product_title", title.getText().toString());
                            productData.put("product_description", desc.getText().toString());
                            productData.put("cutted_price", cutted_price.getText().toString());
                            productData.put("product_image", "");
                            productData.put("Category_Id", categoryIdList.get(id_cate));
                            productData.put("Brand_Id", brandIdList.get(id_brand));
                            ArrayList<String> tag = new ArrayList<>();
                            tag.add(categoryNameList.get(id_cate));
                            tag.add(brandNameList.get(id_brand));
                            tag.add(title.getText().toString());
                            tag.add(categoryNameList.get(id_cate).toUpperCase());
                            tag.add(categoryNameList.get(id_cate).toLowerCase());
                            tag.add(brandNameList.get(id_brand).toLowerCase());
                            tag.add(brandNameList.get(id_brand).toUpperCase());
                            productData.put("tags", tag);

                            Map<String, Object> home_item = new HashMap<>();
                            home_item.put("product_image", "");
                            home_item.put("product_title", title.getText().toString());
                            home_item.put("product_price", price.getText().toString());

                            Map<String, Object> cate_item = new HashMap<>();
                            cate_item.put("product_image", "");
                            cate_item.put("product_subtitle", "Hàng chính hãng");
                            cate_item.put("product_title", title.getText().toString());
                            cate_item.put("product_price", price.getText().toString());
                            updateFields(cate_id, brand_id, productData, home_item, cate_item);
                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else {
            Map<String, Object> productData = new HashMap<>();
            productData.put("product_price", price.getText().toString());
            productData.put("product_title", title.getText().toString());
            productData.put("product_description", desc.getText().toString());
            productData.put("cutted_price", cutted_price.getText().toString());
            productData.put("Category_Id", categoryIdList.get(id_cate));
            productData.put("Brand_Id", brandIdList.get(id_brand));
            ArrayList<String> tag = new ArrayList<>();
            tag.add(categoryNameList.get(id_cate));
            tag.add(brandNameList.get(id_brand));
            tag.add(title.getText().toString());
            tag.add(categoryNameList.get(id_cate).toUpperCase());
            tag.add(categoryNameList.get(id_cate).toLowerCase());
            tag.add(brandNameList.get(id_brand).toLowerCase());
            tag.add(brandNameList.get(id_brand).toUpperCase());
            productData.put("tags", tag);

            Map<String, Object> home_item = new HashMap<>();
            home_item.put("product_title", title.getText().toString());
            home_item.put("product_price", price.getText().toString());

            Map<String, Object> cate_item = new HashMap<>();
            cate_item.put("product_image", productModel.getProductImage());
            cate_item.put("product_subtitle", "Hàng chính hãng");
            cate_item.put("product_title", title.getText().toString());
            cate_item.put("product_price", price.getText().toString());
            updateFields(cate_id, brand_id, productData, home_item, cate_item);
        }
    }

    private void updateFields(String cate_id, String brand_id, final Map<String, Object> productData, final Map<String, Object> home_item, final Map<String, Object> cate_item){
        db.collection("PRODUCTS").document(productModel.getProductID())
                .update(productData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            db.collection("CATEGORIES").document(productModel.getCategory_Id()).collection("BRAND")
                                    .document(productModel.getBrand_Id())
                                    .collection("ITEMS")
                                    .document(productModel.getProductID()).delete();
                            db.collection("CATEGORIES").document(cate_id).collection("BRAND")
                                    .document(brand_id)
                                    .collection("ITEMS")
                                    .document(productModel.getProductID()).set(cate_item);
                            db.collection("CATEGORIES").document("HOME").collection("TOP_DEALS")
                                    .document("HCbOkJXjK7jRqkBe77oj")
                                    .collection("ITEMS")
                                    .document(productModel.getProductID())
                                    .update(home_item);
                            DBqueries.productModelList.clear();
                            Toasty.success(getApplicationContext(),"Cập nhật thành công !!!", Toasty.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toasty.success(getApplicationContext(),"Cập nhật thất bại !!!", Toasty.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}