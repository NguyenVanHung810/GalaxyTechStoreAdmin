package com.example.galaxytechstoreadmin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private List<CategoryModel> categoryModels;

    public CategoryAdapter(List<CategoryModel> categoryModels) {
        this.categoryModels = categoryModels;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoryModel categoryModel = categoryModels.get(position);

        String cate_id = categoryModel.getCategoryId();
        String cate_image = categoryModel.getCategoryImage();
        String cate_name = categoryModel.getCategoryName();
        Long index = categoryModel.getIndex();
        holder.setData(cate_id, cate_image, cate_name, position, index);
    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView cateImage;
        private TextView cateName;
        private ImageButton delete, edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cateImage = (ImageView) itemView.findViewById(R.id.cate_img);
            cateName = (TextView) itemView.findViewById(R.id.cate_name);
            delete = (ImageButton) itemView.findViewById(R.id.btn_del);
            edit = (ImageButton) itemView.findViewById(R.id.btn_edit);
        }

        private void setData(final String id, final String imageUrl, final String name,final int position, long index){
            if(!imageUrl.equals("null")){
                Glide.with(itemView.getContext()).load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.no_img)).into(cateImage);
            }
            else {
                cateImage.setImageResource(R.drawable.no_img);
            }

            cateName.setText(name);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore.getInstance().collection("CATEGORIES").document(id)
                            .collection("BRAND")
                            .orderBy("index")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                int size = 0;
                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    size++;
                                }
                                if (size > 0) {
                                    Toasty.error(itemView.getContext(), "Danh mục bị ràng buộc", Toasty.LENGTH_SHORT).show();
                                }
                                else {
                                    FirebaseStorage.getInstance().getReference().child("category_image/")
                                            .child("cate_image_"+ String.valueOf(index) + ".jpg").delete();
                                    FirebaseFirestore.getInstance().collection("CATEGORIES").document(id)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toasty.success(itemView.getContext(), "Xóa thành công", Toasty.LENGTH_SHORT).show();
                                                    DBqueries.categoryModelList.clear();
                                                    DBqueries.s = 0;
                                                    DBqueries.loadCategoryList(itemView.getContext(), CategoryFragment.loaddialog);
                                                    CategoryFragment.categoryAdapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toasty.error(itemView.getContext(), "Xóa thất bại", Toasty.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }
                    });
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
