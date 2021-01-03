package com.example.galaxytechstoreadmin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.Viewholder>{
    private List<BrandModel> brandModelList;

    public BrandAdapter(List<BrandModel> brandModelList) {
        this.brandModelList = brandModelList;
    }

    @NonNull
    @Override
    public BrandAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_item_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandAdapter.Viewholder holder, int position) {

        String id = brandModelList.get(position).getBrand_id();
        String name=brandModelList.get(position).getBrand_name();
        String cate_id = brandModelList.get(position).getCate_id();


        holder.setdata(id, name, position, cate_id);
    }

    @Override
    public int getItemCount() {
        return brandModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView brand_name;
        private Button edit, delete;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            brand_name = (TextView) itemView.findViewById(R.id.brandname_item);
            edit = (Button) itemView.findViewById(R.id.edit);
            delete = (Button) itemView.findViewById(R.id.delete);

        }

        private void setdata(String id, String name, final int position, String cate_id) {
            brand_name.setText(name);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent updateBrandIntent = new Intent(itemView.getContext(), UpdateBrandActivity.class);
                    updateBrandIntent.putExtra("name", name);
                    updateBrandIntent.putExtra("id", id);
                    updateBrandIntent.putExtra("cate_id", cate_id);
                    itemView.getContext().startActivity(updateBrandIntent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete.setTextColor(Color.argb(50, 255, 255, 255));
                    delete.setEnabled(false);
                    FirebaseFirestore.getInstance().collection("CATEGORIES")
                            .document(cate_id)
                            .collection("BRAND")
                            .document(id)
                            .collection("ITEMS")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                int size = 0;
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    size++;
                                }
                                if (size > 0) {
                                    Toasty.error(itemView.getContext(), "Thương hiệu bị ràng buộc", Toasty.LENGTH_SHORT).show();
                                    ((Activity)itemView.getContext()).finish();
                                }
                                else {
                                    FirebaseFirestore.getInstance().collection("CATEGORIES")
                                            .document(cate_id)
                                            .collection("BRAND")
                                            .document(id).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toasty.success(itemView.getContext(), "Xóa thành công", Toasty.LENGTH_SHORT).show();
                                                    ((Activity)itemView.getContext()).finish();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toasty.success(itemView.getContext(), "Xóa thất bại", Toasty.LENGTH_SHORT).show();
                                                    ((Activity)itemView.getContext()).finish();
                                                }
                                            });
                                }
                            }
                        }
                    });
                }
            });
        }
    }
}
