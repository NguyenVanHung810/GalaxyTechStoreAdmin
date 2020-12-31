package com.example.galaxytechstoreadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class UpdateOrderStatusActivity extends AppCompatActivity {

    private Spinner statusList;
    private Button update, cancel;
    private ArrayList<String> order_status_list;
    private String status;
    private Intent intent;

    private SimpleDateFormat simpleDateFormat;

    private TextView orderId, ordered_date, orderstatus;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order_status);

        statusList = findViewById(R.id.spinner_status);
        update = findViewById(R.id.update_btn);
        cancel = findViewById(R.id.cancel_btn);
        order_status_list = new ArrayList<>();
        intent = getIntent();

        orderId = findViewById(R.id.order_id);
        ordered_date = findViewById(R.id.ordered_date);
        orderstatus = findViewById(R.id.order_status);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cập nhật tình trạng đơn hàng");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        position = getIntent().getIntExtra("position", -1);
        final OrderItemModel orderItemModel = DBqueries.orderItemModelList.get(position);
        order_status_list = getIntent().getStringArrayListExtra("statusList");

        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY, hh:mm aa");

        orderId.setText("Mã đơn hàng: "+ orderItemModel.getOrderId());
        ordered_date.setText("Ngày đặt hàng: "+simpleDateFormat.format(orderItemModel.getOrderedDate()));
        orderstatus.setText(orderItemModel.getOrderStatus());

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, order_status_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusList.setAdapter(adapter);

        statusList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
                ((TextView) parent.getChildAt(0)).setTextSize(16);
                status = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("ORDERS").document(intent.getStringExtra("id"))
                        .update("Order_Status", status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toasty.success(getApplicationContext(), "Cập nhật thành công !!!", Toasty.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toasty.error(getApplicationContext(), "Cập nhật không thành công !!!", Toasty.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}