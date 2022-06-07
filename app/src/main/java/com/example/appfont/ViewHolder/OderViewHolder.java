package com.example.appfont.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfont.Interface.ItemClickListner;
import com.example.appfont.R;

public class OderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener

{
    public TextView orderName, orderMail, orderAddress, orderTotalPrice, orderSdt, orderTime;
    public ItemClickListner listner;
    public OderViewHolder(@NonNull  View itemView) {
        super(itemView);
        orderName = (TextView) itemView.findViewById(R.id.oder_name_admin);
        orderMail = (TextView) itemView.findViewById(R.id.oder_email_admin);
        orderAddress = (TextView) itemView.findViewById(R.id.oder_add_admin);
        orderTotalPrice = (TextView) itemView.findViewById(R.id.oder_price_admin);
        orderSdt = (TextView) itemView.findViewById(R.id.oder_phone_admin);
        orderTime = (TextView) itemView.findViewById(R.id.oder_time_admin);
    }
    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }
    @Override
    public void onClick(View v) {
        listner.onClick(v, getAdapterPosition(),false);


    }
}
