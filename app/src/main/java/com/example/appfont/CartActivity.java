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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appfont.Model.Cart;
import com.example.appfont.Model.PriceSplit;
import com.example.appfont.Model.Products;
import com.example.appfont.Prevalent.Prevalent;
import com.example.appfont.ViewHolder.CartViewHolder;
import com.example.appfont.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn, totalPrice_bt;
    private TextView txtTotalAmout, totalPrice;
    private DatabaseReference Cartref, Cartref2;
    private int TotalPrice_int = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Cartref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products");
        recyclerView = findViewById(R.id.cart_list);
        layoutManager = new LinearLayoutManager(this);
        totalPrice = findViewById(R.id.total_price_txt);
        NextProcessBtn = findViewById(R.id.next_cart);
        totalPrice_bt = findViewById(R.id.total_price_bt);
        NextProcessBtn = findViewById(R.id.next_cart);
        txtTotalAmout = findViewById(R.id.total_price_txt);
        TextView back = findViewById(R.id.close_settings_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
//        back = findViewById(R.id.back_bt_cart);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
//                intent.putExtra("total", String.valueOf(TotalPrice_int));
//                startActivity(intent);
//            }
//        });
        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ki = String.valueOf(String.valueOf(TotalPrice_int));
                ki = PriceSplit.Nam(ki);
                txtTotalAmout.setText("Giá: " + ki + " đồng");
                Intent intent = new Intent(CartActivity.this, ConfirmFormOrderActivity.class);
                intent.putExtra("total", String.valueOf(TotalPrice_int));
                startActivity(intent);
            }
        });
        totalPrice_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ki = String.valueOf(String.valueOf(TotalPrice_int));
                ki = PriceSplit.Nam(ki);
                txtTotalAmout.setText("Giá: " + ki + " đồng");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(Cartref, Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {


                        holder.txtProductName.setText(model.getPname());
                        String ssk = PriceSplit.Nam(model.getPrice());
                        holder.txtProductPrice.setText("Giá: " + ssk + " đồng");
                        holder.txtCategory.setText(model.getCategory());
                        Glide.with(CartActivity.this).load(model.getImage()).into(holder.imageView);
                        TotalPrice_int = TotalPrice_int + Integer.parseInt(model.getPrice());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                {
                                    CharSequence option[] = new CharSequence[]{
                                            "Xem chi tiết",
                                            "Xóa khỏi giỏ hàng"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                    builder.setTitle("Tùy chọn");
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, final int i) {
                                            if (i == 0) {
                                                Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                                intent.putExtra("pid", model.getPid());
                                                startActivity(intent);
                                            }
                                            if (i == 1) {
                                                Cartref2 = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products");
                                                Cartref2.child(model.getPid())
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(CartActivity.this, "Xóa khỏi giỏ hàng thành công", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                    builder.show();
                                }
//
                            }
                        });
                    }

                    //
                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        TextView txtProductName;
                        TextView txtProductPrice,txtCategory;
                        ImageView imageView;
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                        txtProductName = view.findViewById(R.id.cart_product_name);
                        txtProductPrice = view.findViewById(R.id.cart_product_price);
                        imageView = view.findViewById(R.id.cart_image);
                        txtCategory = view.findViewById(R.id.cart_product_category);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}