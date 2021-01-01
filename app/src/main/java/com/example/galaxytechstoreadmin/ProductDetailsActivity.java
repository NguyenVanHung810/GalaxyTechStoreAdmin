package com.example.galaxytechstoreadmin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

import es.dmoral.toasty.Toasty;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView product_image;
    private TextView product_title, average_ratings, total_ratings, product_price, cutted_price, desc;
    private Dialog loaddialog;
    private String productID;
    private FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private ProductDetailsActivity productDetailsActivity;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Thông tin chi tiết sản phẩm");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        product_image = (ImageView) findViewById(R.id.product_detail_image);
        product_title = (TextView) findViewById(R.id.product_title);
        average_ratings = (TextView) findViewById(R.id.average_ratings);
        total_ratings = (TextView) findViewById(R.id.total_ratings);
        product_price = (TextView) findViewById(R.id.product_price);
        cutted_price = (TextView) findViewById(R.id.cutted_price);
        desc = (TextView) findViewById(R.id.product_desc);

        // Loading dialog
        loaddialog = new Dialog(ProductDetailsActivity.this);
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.show();
        // Loading dialog

        productID = getIntent().getStringExtra("PRODUCT_ID");

        DB.collection("PRODUCTS").document(productID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Glide.with(getApplicationContext()).load(task.getResult().get("product_image").toString()).apply(new RequestOptions().placeholder(R.drawable.no_img)).into(product_image);
                    product_title.setText(task.getResult().get("product_title").toString());
                    average_ratings.setText(task.getResult().get("average_ratings").toString());
                    total_ratings.setText(task.getResult().get("total_ratings").toString() + " đánh giá");
                    product_price.setText(vnMoney(Long.parseLong(task.getResult().get("product_price").toString())));
                    cutted_price.setText(vnMoney(Long.parseLong(task.getResult().get("cutted_price").toString())));
                    desc.setText(task.getResult().get("product_description").toString());
                }
                else {
                    String error = task.getResult().toString();
                    Toasty.error(getApplicationContext(), error, Toasty.LENGTH_SHORT).show();
                }
                loaddialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        productDetailsActivity = null;
        super.onBackPressed();
    }

    private String vnMoney(Long s) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(s) + " đ";
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}