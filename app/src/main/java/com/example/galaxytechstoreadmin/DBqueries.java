package com.example.galaxytechstoreadmin;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class DBqueries {

    public static String email,fullname, phone;
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<ProductModel> productModelList = new ArrayList<>();
    public static List<Long> longList = new ArrayList<>();
    public static long index;

    public static List<OrderItemModel> orderItemModelList = new ArrayList<>();

    public static void clearData(){
        productModelList.clear();
        longList.clear();
        orderItemModelList.clear();
    }

    public static void loadProductList(Context context, Dialog dialog) {
        productModelList.clear();
        firebaseFirestore.collection("PRODUCTS").orderBy("index")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        productModelList.add(new ProductModel(
                                documentSnapshot.getId(),
                                documentSnapshot.get("product_image").toString(),
                                documentSnapshot.get("product_description").toString(),
                                documentSnapshot.get("product_title").toString(),
                                documentSnapshot.get("average_ratings").toString(),
                                (long) documentSnapshot.get("total_ratings"),
                                documentSnapshot.get("product_price").toString(),
                                documentSnapshot.get("cutted_price").toString(),
                                (boolean) documentSnapshot.get("COD"),
                                (boolean) documentSnapshot.get("in_stock")
                        ));
                        index = (long)documentSnapshot.get("index");
                    }
                    ProductFragment.productAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else {
                    dialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void loadOrderList(Context context, OrderItemAdapter orderItemAdapter, Dialog dialog){
        firebaseFirestore.collection("ORDERS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot: task.getResult()){
                        ArrayList<ProductsInOrderModel> products = new ArrayList<>();
                        firebaseFirestore.collection("ORDERS").document(documentSnapshot.getId())
                                .collection("ORDER_ITEMS").orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(DocumentSnapshot snapshot: task.getResult()){
                                    products.add(new ProductsInOrderModel(
                                            snapshot.get("Product_Image").toString(),
                                            snapshot.get("Product_Title").toString(),
                                            snapshot.get("Product_Price").toString(),
                                            (long)snapshot.get("Product_quantity"),
                                            snapshot.get("Cutted_Price").toString()));
                                }
                            }
                        });
                        OrderItemModel orderItemModel = new OrderItemModel(
                                documentSnapshot.getId(),
                                documentSnapshot.getString("Order_Status"),
                                documentSnapshot.getString("Address"),
                                documentSnapshot.getString("FullName"),
                                documentSnapshot.getString("Phone_Number"),
                                (Date) documentSnapshot.getDate("Ordered_Date"),
                                (Date) documentSnapshot.getDate("Packed_Date"),
                                (Date) documentSnapshot.getDate("Shipped_Date"),
                                (Date) documentSnapshot.getDate("Delivered_Date"),
                                (Date) documentSnapshot.getDate("Cancelled_Date"),
                                documentSnapshot.getString("Payment_Method"),
                                documentSnapshot.getString("Delivery_Price"),
                                (boolean)documentSnapshot.get("Cancellation_requested"),
                                (long) documentSnapshot.get("Total_Items"),
                                documentSnapshot.get("Discounted_Price").toString(),
                                (Long) documentSnapshot.get("Total_Items_Price"),
                                products
                        );
                        orderItemModelList.add(orderItemModel);
                    }
                    orderItemAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else {
                    String error = task.getException().toString();
                    Toasty.error(context, error, Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }
}
