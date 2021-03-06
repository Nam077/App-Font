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

import com.bumptech.glide.Glide;
import com.example.appfont.Model.PriceSplit;
import com.example.appfont.Model.Product_Cart;
import com.example.appfont.ViewHolder.OderProductHolderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserViewOrderProduct extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private String OrderId = "";
    private Button backOrderProduct;
    private DatabaseReference orderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_order_product);
        recyclerView = findViewById(R.id.product_order_list);
        OrderId = getIntent().getStringExtra("idOder");
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        orderView = FirebaseDatabase.getInstance().getReference().child("Orders Product").child(OrderId).child("Products");
        TextView back = findViewById(R.id.close_settings_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserViewOrderProduct.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Product_Cart> options = new FirebaseRecyclerOptions.Builder<Product_Cart>()
                .setQuery(orderView, Product_Cart.class).build();
        FirebaseRecyclerAdapter<Product_Cart, OderProductHolderView> adapter =
                new FirebaseRecyclerAdapter<Product_Cart, OderProductHolderView>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OderProductHolderView holder, int position, @NonNull  Product_Cart model) {
                        holder.txtProductName.setText(model.getPname());
                        String ssk = PriceSplit.Nam(model.getPrice());
                        holder.txtProductPrice.setText("Gi??: " + ssk + " ?????ng");
                        holder.txtCategory.setText(model.getCategory());
                        Glide.with(UserViewOrderProduct.this).load(model.getImage()).into(holder.imageView);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                {
                                    CharSequence option[] = new CharSequence[]{
                                            "Xem chi ti???t",
                                            "X??a kh???i gi??? h??ng"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserViewOrderProduct.this);
                                    builder.setTitle("T??y ch???n");
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, final int i) {
                                            if (i == 0) {
//                                                Intent intent = new Intent(AdminViewProductOrder.this, ProductDetailsActivity.class);
//                                                intent.putExtra("pid", model.getPid());
//                                                startActivity(intent);
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
                    public OderProductHolderView onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                        TextView txtProductName;
                        TextView txtProductPrice,txtCategory;
                        ImageView imageView;
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                        txtProductName = view.findViewById(R.id.cart_product_name);
                        txtProductPrice = view.findViewById(R.id.cart_product_price);
                        imageView = view.findViewById(R.id.cart_image);
                        txtCategory = view.findViewById(R.id.cart_product_category);
                        OderProductHolderView holder = new OderProductHolderView(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}