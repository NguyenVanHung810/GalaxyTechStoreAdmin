package com.example.galaxytechstoreadmin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import es.dmoral.toasty.Toasty;


public class ProductFragment extends Fragment {

    private RecyclerView productRecycleView;
    public static Dialog deletedialog;
    public static ProductAdapter productAdapter;
    public static Dialog loaddialog;

    public static Button yes;
    public static Button no;

    public ProductFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        setHasOptionsMenu(true);
        productRecycleView = (RecyclerView) view.findViewById(R.id.product_recyclerview);

        loaddialog = new Dialog(view.getContext());
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.show();

        // -- Delete dialog
        deletedialog = new Dialog(view.getContext());
        deletedialog.setContentView(R.layout.confirm_delete_dialog);
        deletedialog.setCancelable(false);
        deletedialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        deletedialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        yes = (Button) deletedialog.findViewById(R.id.yes_btn);
        no  =(Button) deletedialog.findViewById(R.id.no_btn);
        // -- Delete dialog

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.onSaveInstanceState();
        productRecycleView.setLayoutManager(layoutManager);

        productAdapter = new ProductAdapter(DBqueries.productModelList);
        productRecycleView.setAdapter(productAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DBqueries.productModelList.clear();
        loaddialog.show();
        if(DBqueries.productModelList.size() == 0){
            DBqueries.loadProductList(getContext(), loaddialog);
        }
        else {
            loaddialog.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_menu_ic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_product:
                startActivity(new Intent(getContext(), AddProductActivity.class));
                return false;
            case R.id.search_product:
                startActivity(new Intent(getContext(), SearchActivity.class));
                return false;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}