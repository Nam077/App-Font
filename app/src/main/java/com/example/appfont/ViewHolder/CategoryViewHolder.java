package com.example.appfont.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfont.Interface.ItemClickListner;
import com.example.appfont.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtCatregory;
    public ImageView imageCategory;
    public ItemClickListner listner;
    public CategoryViewHolder(@NonNull  View itemView) {
        super(itemView);
        txtCatregory =  itemView.findViewById(R.id.title_category);
        imageCategory =  itemView.findViewById(R.id.image_category);
    }

    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(), false);
    }
}
