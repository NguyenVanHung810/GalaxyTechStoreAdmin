package com.example.galaxytechstoreadmin;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DBqueries {
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<ProductModel> productModelList = new ArrayList<>();
    public static List<Long> longList = new ArrayList<>();
    public static long index;

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
}
