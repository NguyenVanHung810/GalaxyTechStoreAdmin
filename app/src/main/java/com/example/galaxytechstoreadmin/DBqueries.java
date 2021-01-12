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
    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static List<Long> longList = new ArrayList<>();
    public static long product_index;
    public static long cate_index;
    public static long brand_index;
    public static int s = 0;

    public static int productTotals = 0;
    public static int userTotals = 0;
    public static int cateTotals = 0;
    public static int orderTotals = 0;

    public static List<OrderItemModel> orderItemModelList = new ArrayList<>();

    public static void clearData(){
        productModelList.clear();
        longList.clear();
        orderItemModelList.clear();
        categoryModelList.clear();
    }

    public static void loadCategoryList(Context context, Dialog dialog) {
        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES")
                .orderBy("index")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        brand_index = 0;
                        if(s==0){
                            s = -1;
                        }
                        else {
                            List<BrandModel> brandModelList = new ArrayList<>();
                            firebaseFirestore.collection("CATEGORIES").document(documentSnapshot.getId()).collection("BRAND")
                                    .orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for (DocumentSnapshot snapshot: task.getResult()){
                                            brandModelList.add(new BrandModel(
                                                    snapshot.getId(),
                                                    snapshot.get("layout_title").toString(),
                                                    documentSnapshot.getId()
                                            ));
                                            brand_index = (long)snapshot.get("index");
                                        }
                                    }
                                }
                            });
                            categoryModelList.add(new CategoryModel(
                                    documentSnapshot.getId(),
                                    documentSnapshot.get("icon").toString(),
                                    documentSnapshot.get("categoryName").toString(),
                                    (long)documentSnapshot.get("index"),
                                    brand_index,
                                    brandModelList
                            ));
                        }
                        cate_index = (long) documentSnapshot.get("index");
                    }
                    CategoryFragment.categoryAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                                (boolean) documentSnapshot.get("in_stock"),
                                documentSnapshot.get("Category_Id").toString(),
                                documentSnapshot.get("Brand_Id").toString(),
                                (long) documentSnapshot.get("index")
                        ));
                        product_index = (long)documentSnapshot.get("index");
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
                                .collection("ORDER_ITEMS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                documentSnapshot.getString("User_Id"),
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

    public static void getUserTotals() {
        firebaseFirestore.collection("USERS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                    userTotals++;
                }
            }
        });
    }

    public static void getProductTotals() {
        firebaseFirestore.collection("PRODUCTS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                    productTotals++;
                }
            }
        });
    }

    public static void getOrderTotals() {
        firebaseFirestore.collection("ORDERS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                    orderTotals++;
                }
            }
        });
    }

    public static void getCateTotals() {
        firebaseFirestore.collection("CATEGORIES").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                    cateTotals++;
                }
            }
        });
    }
}
