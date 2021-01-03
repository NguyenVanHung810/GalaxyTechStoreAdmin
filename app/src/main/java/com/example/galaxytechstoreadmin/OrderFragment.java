package com.example.galaxytechstoreadmin;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class OrderFragment extends Fragment {

    private RecyclerView orderRecycleView;
    public Dialog loadingDialog;
    private OrderItemAdapter orderItemAdapter;
    private View view;

    public OrderFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order, container, false);
        }
        orderRecycleView = (RecyclerView) view.findViewById(R.id.order_recyclerview);

        //////////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        //////////loading dialog

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.onSaveInstanceState();
        orderRecycleView.setLayoutManager(layoutManager);

        orderItemAdapter = new OrderItemAdapter(DBqueries.orderItemModelList, loadingDialog);
        orderRecycleView.setAdapter(orderItemAdapter);
        orderItemAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        orderItemAdapter.notifyDataSetChanged();
        loadingDialog.show();
        DBqueries.orderItemModelList.clear();
        if(DBqueries.orderItemModelList.size() ==0){
            DBqueries.loadOrderList(getContext(), orderItemAdapter, loadingDialog);
        }
        else {
            loadingDialog.dismiss();
        }
    }
}