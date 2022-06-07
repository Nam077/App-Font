package com.example.appfont.ViewHolder;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfont.Interface.ItemClickListner;
import com.example.appfont.R;

public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public ImageView imageView;
    public ItemClickListner listner;

    public ImageViewHolder(@NonNull View itemView) {

        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image_items);
    }

    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(), false);

    }
}
