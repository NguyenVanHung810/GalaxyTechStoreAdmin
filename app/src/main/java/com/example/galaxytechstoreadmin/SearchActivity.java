package com.example.galaxytechstoreadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private Button back;
    private androidx.appcompat.widget.SearchView searchView;
    private RecyclerView recyclerView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView=findViewById(R.id.search_view);
        textView=findViewById(R.id.search_textView);
        imageView = findViewById(R.id.search_image_no_data);
        back = findViewById(R.id.back);
        recyclerView=findViewById(R.id.search_recyclerview);

        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        final List<ProductModel> list=new ArrayList<>();
        final List<String> ids=new ArrayList<>();

        adapter=new Adapter(list);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                list.clear();
                ids.clear();
                final String[] tags = s.toLowerCase().split(" ");
                for (final String tag : tags) {
                    tag.trim();
                    FirebaseFirestore.getInstance().collection("PRODUCTS").whereArrayContains("tags", tag)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                    ProductModel model = new ProductModel(
                                            documentSnapshot.getId()
                                            , documentSnapshot.get("product_image").toString()
                                            , documentSnapshot.get("product_description").toString()
                                            , documentSnapshot.get("product_title").toString()
                                            , documentSnapshot.get("average_ratings").toString()
                                            , (long) documentSnapshot.get("total_ratings")
                                            , documentSnapshot.get("product_price").toString()
                                            , documentSnapshot.get("cutted_price").toString()
                                            , (boolean) documentSnapshot.get("COD")
                                            , true
                                            , documentSnapshot.getString("Category_Id").toString()
                                            , documentSnapshot.getString("Brand_Id").toString()
                                            , (long) documentSnapshot.get("index")
                                    );

                                    model.setTags((ArrayList<String>) documentSnapshot.get("tags"));

                                    if (!ids.contains(model.getProductID())) {
                                        list.add(model);
                                        ids.add(model.getProductID());
                                    }
                                }
                                if (tag.equals(tags[tags.length - 1])) {
                                    if (list.size() == 0) {
                                        textView.setVisibility(View.VISIBLE);
                                        imageView.setVisibility(View.VISIBLE);
                                        back.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    } else {
                                        textView.setVisibility(View.GONE);
                                        imageView.setVisibility(View.GONE);
                                        back.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        adapter.getFilter().filter(s);
                                    }
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SearchActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    class Adapter extends ProductAdapter implements Filterable {

        private List<ProductModel> originalList;

        public Adapter(List<ProductModel> productModelList) {
            super(productModelList);
            originalList = productModelList;
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults filterResults=new FilterResults();
                    List<ProductModel> filteredList=new ArrayList<>();
                    final String[] tags=charSequence.toString().toLowerCase().split(" ");

                    for(ProductModel model:originalList){
                        ArrayList<String> presentTags = new ArrayList<>();
                        for( String tag:tags){
                            if(model.getTags().contains(tag)){
                                presentTags.add(tag);
                            }
                        }
                        model.setTags(presentTags);
                    }
                    for(int i=tags.length;i>0;i--){
                        for(ProductModel model:originalList){
                            if(model.getTags().size() ==  i){
                                filteredList.add(model);
                            }
                        }
                    }
                    filterResults.values=filteredList;
                    filterResults.count=filteredList.size();


                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                    if(filterResults.count>0){
                        setProductModelList((List<ProductModel>) filterResults.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
    }
}