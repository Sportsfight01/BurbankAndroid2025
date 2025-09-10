package com.dmss.burbankapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dmss.burbankapp.data.model.BreadcrumbModel;
import com.dmss.burbankapp.databinding.LayoutBreadcrumbRecyclerviewBinding;

import java.util.ArrayList;


public class CustomRecyclerView extends RelativeLayout {
    private BreadCrumbAdapter breadCrumbAdapter;
    private Context context;

    private LayoutBreadcrumbRecyclerviewBinding binding;

    public void initView(Context context) {
        this.context = context;
        init(context, null);
    }


    public CustomRecyclerView(@NonNull Context context) {
        super(context);
        this.context = context;
        init(context, null);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = LayoutBreadcrumbRecyclerviewBinding.inflate(inflater, null, false);
        addView(binding.getRoot());
    }


    public void setData(ArrayList<BreadcrumbModel> breadCrumbList, BreadCrumbAdapter.BreadcrumbItemClickListener breadcrumbItemClickListener) {
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        breadCrumbAdapter = new BreadCrumbAdapter(context, breadCrumbList, breadcrumbItemClickListener);
        binding.recyclerView.setAdapter(breadCrumbAdapter);
        if (breadCrumbList.size() > 1) {
            binding.recyclerView.scrollToPosition(breadCrumbList.size() - 1);
        }

    }

}
