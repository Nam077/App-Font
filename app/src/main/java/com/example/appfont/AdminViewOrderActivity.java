package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appfont.Interface.ItemClickListner;
import com.example.appfont.Model.AdminOrders;
import com.example.appfont.Model.PriceSplit;
import com.example.appfont.ViewHolder.CartViewHolder;
import com.example.appfont.ViewHolder.OderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminViewOrderActivity extends AppCompatActivity {
        private RecyclerView oderlist;
        private DatabaseReference oderRef;
        private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_order);
        oderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        oderlist = findViewById(R.id.oder_admin_list);
        layoutManager = new LinearLayoutManager(this);
        oderlist.setLayoutManager(layoutManager);
        oderlist.setLayoutManager(new LinearLayoutManager(this));
        TextView back = findViewById(R.id.close_settings_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminViewOrderActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(oderRef, AdminOrders.class).build();
                FirebaseRecyclerAdapter<AdminOrders, OderViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, OderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull  OderViewHolder holder, int position, @NonNull AdminOrders model) {
                        holder.orderName.setText("Họ Và Tên: "+model.getName());
                        holder.orderMail.setText("Email: "+model.getEmail());
                        holder.orderSdt.setText("Số Điện Thoại: "+model.getPhone());
                        holder.orderTotalPrice.setText("Tồng Tiền: "+ PriceSplit.Nam(model.getTotalAmount()));
                        holder.orderAddress.setText("Địa Chỉ: "+model.getAddress());
                        holder.orderTime.setText("Thời Gian Đặt: "+model.getTime()+" " + model.getDate());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                {
                                    CharSequence option[] = new CharSequence[]{
                                            "Xem chi tiết",
                                            "Xóa khỏi giỏ hàng"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminViewOrderActivity.this);
                                    builder.setTitle("Tùy chọn");
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, final int i) {
                                            if (i == 0) {

                                                Intent intent = new Intent(AdminViewOrderActivity.this, AdminViewProductOrder.class);
                                                intent.putExtra("idOder", model.getId());
                                                startActivity(intent);
                                            }
                                            if (i == 1) {

                                            }
                                        }
                                    });
                                    builder.show();

                                }
//
                            }
                        });

                    }

                    @NonNull

                    @Override
                    public OderViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                        TextView orderName, orderMail, orderAddress, orderTotalPrice, orderSdt, orderTime;
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.oder_layout,parent,false);
                        orderName = view.findViewById(R.id.oder_name_admin);
                        orderMail = view.findViewById(R.id.oder_email_admin);
                        orderAddress = view.findViewById(R.id.oder_add_admin);
                        orderTotalPrice = view.findViewById(R.id.oder_price_admin);
                        orderSdt = view.findViewById(R.id.oder_phone_admin);
                        orderTime = view.findViewById(R.id.oder_time_admin);
                        OderViewHolder holder = new OderViewHolder(view);
                        return holder;
                    }
                };
        oderlist.setAdapter(adapter);
        adapter.startListening();
    }
}

