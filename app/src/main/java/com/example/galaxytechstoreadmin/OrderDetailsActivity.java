package com.example.galaxytechstoreadmin;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {

    private SimpleDateFormat simpleDateFormat;
    private Dialog loadingDialog, cancelDialog;
    private int position;
    private ImageView orderedIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_progress, P_S_progress, S_D_progress;
    private TextView orderedTitle, packedTitle, shippedTitle, deliveredTitle;
    private TextView orderedDate, shippedDate, packedDate, deliveredDate;
    private TextView orderedBody, shippedBody, packedBody, deliveredBody;
    private TextView fullname, fullladdress, phonenumber;
    private TextView totalItems, totalItemsPrice, deliveryPrice, savedAmount, totalAmount;

    private TextView orderId, ordered_Date, orderStatus;

    private Button view;

    public static ArrayList<ProductsInOrderModel> products;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        position = getIntent().getIntExtra("position", -1);
        final OrderItemModel orderItemModel = DBqueries.orderItemModelList.get(position);
        products = orderItemModel.getProducts();

        orderedIndicator = findViewById(R.id.ordered_indicator);
        packedIndicator = findViewById(R.id.packed_indicator);
        shippedIndicator = findViewById(R.id.shipping_indicator);
        deliveredIndicator = findViewById(R.id.delivered_indicator);

        O_P_progress = findViewById(R.id.ordered_packed_progress);
        P_S_progress = findViewById(R.id.packed_shipping_progress);
        S_D_progress = findViewById(R.id.shipping_delivered_progress);

        orderedTitle = findViewById(R.id.ordered_title);
        packedTitle = findViewById(R.id.packed_title);
        shippedTitle = findViewById(R.id.shipping_title);
        deliveredTitle = findViewById(R.id.delivered_title);

        orderedBody = findViewById(R.id.ordered_body);
        packedBody = findViewById(R.id.packed_body);
        shippedBody = findViewById(R.id.shipping_body);
        deliveredBody = findViewById(R.id.delivered_body);

        orderedDate = findViewById(R.id.ordered_date);
        packedDate = findViewById(R.id.packed_date);
        shippedDate = findViewById(R.id.shipping_date);
        deliveredDate = findViewById(R.id.delivered_date);

        fullname = findViewById(R.id.fullname);
        fullladdress = findViewById(R.id.fulladdress);
        phonenumber = findViewById(R.id.phone_number);

        totalItems = findViewById(R.id.total_items);
        totalItemsPrice = findViewById(R.id.total_items_price);
        deliveryPrice = findViewById(R.id.delivery_price);
        totalAmount = findViewById(R.id.total_price);

        orderId = findViewById(R.id.order_id);
        ordered_Date = findViewById(R.id.ordered_date);
        orderStatus = findViewById(R.id.order_status);

        view = findViewById(R.id.view_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Xem chi tiết đơn hàng");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY hh:mm aa");
        orderId.setText("Mã đơn hàng: "+orderItemModel.getOrderId());
        ordered_Date.setText("Ngày đặt hàng: "+ simpleDateFormat.format(orderItemModel.getOrderedDate()));
        orderStatus.setText(orderItemModel.getOrderStatus());


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pioIntent = new Intent(getApplicationContext(), ProductInOrderActivity.class);
                startActivity(pioIntent);
            }
        });

        switch (orderItemModel.getOrderStatus()) {
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getOrderedDate())));

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);

                break;

            case "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getPackedDate())));

                O_P_progress.setProgress(100);

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);


                shippedIndicator.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);

                break;

            case "Shipped":
                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getShippedDate())));

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getPackedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);

                break;
            case "out for Delivery":
                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getDelveredDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getShippedDate())));

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getPackedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                deliveredTitle.setText("Out for Delivery");
                deliveredBody.setText("Đơn hàng đang trên đường giao hàng.");

                break;
            case "Delivered":
                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getDelveredDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getShippedDate())));

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getPackedDate())));

                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                break;

            case "Cancelled":

                if (orderItemModel.getPackedDate().after(orderItemModel.getOrderedDate())) {

                    if (orderItemModel.getShippedDate().after(orderItemModel.getPackedDate())) {

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getDelveredDate())));
                        deliveredBody.setText("Đơn hàng của bạn đã được hủy.");
                        deliveredTitle.setText("Đã hủy");

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getShippedDate())));

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getPackedDate())));

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setProgress(100);

                    } else {
                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getShippedDate())));
                        shippedBody.setText("Đơn hàng của bạn đã được hủy.");
                        shippedTitle.setText("Đã hủy");

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getPackedDate())));

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setVisibility(View.GONE);

                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                    }

                } else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.md_green_500)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(orderItemModel.getPackedDate())));
                    packedBody.setText("Đơn hàng của bạn đã được hủy.");
                    packedTitle.setText("Đã hủy");

                    O_P_progress.setProgress(100);

                    P_S_progress.setVisibility(View.GONE);
                    S_D_progress.setVisibility(View.GONE);


                    shippedIndicator.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);
                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                }
                break;
        }

        fullname.setText(orderItemModel.getFullName());
        fullladdress.setText(orderItemModel.getAddress());
        phonenumber.setText(orderItemModel.getPhoneNumber());

        totalItems.setText("Giá (" + orderItemModel.getTotalItems() + " sản phẩm)");
        if (orderItemModel.getDiscountedPrice().equals("")) {
            totalItemsPrice.setText(vnMoney(Long.parseLong(String.valueOf(orderItemModel.getTotalItemsPrice()))));
        } else {
            totalItemsPrice.setText(vnMoney(Long.parseLong(String.valueOf(orderItemModel.getTotalItemsPrice()))));
        }

        if (orderItemModel.getDeliveryPrice().equals("FREE")) {
            deliveryPrice.setText("FREE");
            totalAmount.setText(vnMoney(orderItemModel.getTotalItemsPrice()));
        } else {
            deliveryPrice.setText(vnMoney(Long.parseLong(orderItemModel.getDeliveryPrice())));
            totalAmount.setText(vnMoney(Long.parseLong(String.valueOf(orderItemModel.getTotalItemsPrice() + Long.valueOf(orderItemModel.getDeliveryPrice())))));
        }

    }

    private String vnMoney(Long s) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(s) + " đ";
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}