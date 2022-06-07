package com.example.appfont.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appfont.Interface.ItemClickListner;
import com.example.appfont.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtProductName, txtProductQuantiti, txtProductPrice,txtCategory;
    public ImageView imageView;
    public ItemClickListner listner;
    public CartViewHolder(View itemView)
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