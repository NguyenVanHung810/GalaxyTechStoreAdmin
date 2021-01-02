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


public class CategoryFragment extends Fragment {

    public static CategoryAdapter categoryAdapter;
    private RecyclerView cateRecyclerView;
    public static Dialog loaddialog;

    public CategoryFragment() {
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        setHasOptionsMenu(true);

        cateRecyclerView = (RecyclerView) view.findViewById(R.id.category_recyclerview);

        loaddialog = new Dialog(view.getContext());
        loaddialog.setContentView(R.layout.loading_progress_dialog);
        loaddialog.setCancelable(false);
        loaddialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaddialog.show();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.onSaveInstanceState();
        cateRecyclerView.setLayoutManager(layoutManager);

        categoryAdapter = new CategoryAdapter(DBqueries.categoryModelList);
        cateRecyclerView.setAdapter(categoryAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DBqueries.categoryModelList.clear();
        DBqueries.s = 0;
        loaddialog.show();
        if(DBqueries.categoryModelList.size() == 0){
            DBqueries.loadCategoryList(getContext(), loaddialog);
        }
        else {
            loaddialog.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_cate_menu_ic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_cate_ic:
                startActivity(new Intent(getContext(), AddCategoryActivity.class));
                return false;
            default:
                break;
        }
        return false;
    }
}