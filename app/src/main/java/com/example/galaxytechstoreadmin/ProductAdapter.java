package com.example.galaxytechstoreadmin;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.Viewholder> {

    private List<ProductModel> productModelList;
    private int lastposition = -1;

    public ProductAdapter(List<ProductModel> productModelList) {
        this.productModelList = productModelList;
    }


    @NonNull
    @Override
    public ProductAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.Viewholder holder, int position) {
        String id = productModelList.get(position).getProductID();
        String url = productModelList.get(position).getProductImage();
        String desc = productModelList.get(position).getProductDesc();
        String tt = productModelList.get(position).getProductTitle();
        String rating = productModelList.get(position).getAverage_Ratings();
        long tr = productModelList.get(position).getTotal_Ratings();
        String pp = productModelList.get(position).getProductPrice();
        String cp = productModelList.get(position).getCuttedPrice();
        boolean pm = productModelList.get(position).getCOD();
        boolean is = productModelList.get(position).getInStock();
        String cate_id = productModelList.get(position).getCategory_Id();
        String brand_id = productModelList.get(position).getBrand_Id();

        holder.setData(id, url, desc, tt, rating, tr, pp, cp, pm, position, is, cate_id, brand_id);

        if (lastposition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastposition = position;
        }
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productTitle;
        private TextView rating;
        private TextView totalRatings;
        private View priceCut;
        private TextView productPrice;
        private TextView cuttedPrice;
        private ImageButton deleteBtn;
        private ImageButton editBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.product_image_wishlist_item);
            productTitle = (TextView) itemView.findViewById(R.id.product_title_wishlist_item);
            rating = (TextView) itemView.findViewById(R.id.rating_wishlist_item);
            totalRatings = (TextView) itemView.findViewById(R.id.total_rating_wishlist_item);
            priceCut = (View) itemView.findViewById(R.id.price_cut);
            productPrice = (TextView) itemView.findViewById(R.id.product_price_wishlist_item);
            cuttedPrice = (TextView) itemView.findViewById(R.id.cutted_price_wishlist_item);
            deleteBtn = (ImageButton) itemView.findViewById(R.id.btn_del);
            editBtn = (ImageButton) itemView.findViewById(R.id.btn_edit);
        }

        private void setData(String id, String url, String desc, String title, String averageRate, long tr, String pp, String cp, boolean pm, int index, boolean instock, final String c_id, final String b_id) {
            Glide.with(itemView.getContext()).load(url).apply(new RequestOptions().placeholder(R.drawable.no_img)).into(productImage);
            productTitle.setText(title);
            LinearLayout linearLayout = (LinearLayout) rating.getParent();
            if (instock) {
                rating.setVisibility(View.VISIBLE);
                totalRatings.setVisibility(View.VISIBLE);
                productPrice.setText("Hết hàng");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setVisibility(View.VISIBLE);
                rating.setText(averageRate);
                totalRatings.setText("(" + tr + " đánh giá)");
                productPrice.setText(convertToVietnameseMoney(Integer.parseInt(pp)));
                cuttedPrice.setText(convertToVietnameseMoney(Integer.parseInt(cp)));
            } else {
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                totalRatings.setVisibility(View.INVISIBLE);
                productPrice.setText("Hết hàng");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.md_red_500));
                cuttedPrice.setVisibility(View.INVISIBLE);
            }

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductFragment.deletedialog.show();
                    ProductFragment.yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseFirestore.getInstance().collection("CATEGORIES")
                                    .document(c_id)
                                    .collection("BRAND")
                                    .document(b_id)
                                    .collection("ITEMS")
                                    .document(id).delete();
                            FirebaseFirestore.getInstance().collection("CATEGORIES")
                                    .document("HOME")
                                    .collection("TOP_DEALS")
                                    .document("HCbOkJXjK7jRqkBe77oj")
                                    .collection("ITEMS")
                                    .document(id).delete();
                            FirebaseFirestore.getInstance().collection("PRODUCTS").document(id)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toasty.success(itemView.getContext(), "Xóa thành công", Toasty.LENGTH_SHORT).show();
                                            DBqueries.productModelList.clear();
                                            DBqueries.loadProductList(itemView.getContext(), ProductFragment.loaddialog);
                                            ProductFragment.productAdapter.notifyDataSetChanged();
                                            ProductFragment.deletedialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toasty.error(itemView.getContext(), "Xóa thất bại", Toasty.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                    ProductFragment.no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProductFragment.deletedialog.dismiss();
                        }
                    });
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        private String convertToVietnameseMoney(int t) {
            Locale locale = new Locale("vi", "VN");
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            return format.format(t);
        }
    }
}
