package com.example.appfont.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfont.Interface.ItemClickListner;
import com.example.appfont.R;

public class OderProductHolderView extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName, txtProductQuantiti, txtProductPrice,txtCategory;
    public ImageView imageView;
    public ItemClickListner listner;
    public OderProductHolderView(View itemView)
    {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.cart_image);
        txtProductName = (TextView) itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = (TextView) itemView.findViewById(R.id.cart_product_price);
        txtCategory = (TextView) itemView.findViewById(R.id.cart_product_category);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {

        listner.onClick(view, getAdapterPosition(), false);
    }
}