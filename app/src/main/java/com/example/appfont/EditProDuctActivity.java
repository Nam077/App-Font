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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appfont.Model.PriceSplit;
import com.example.appfont.Model.Products;
import com.example.appfont.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class EditProDuctActivity extends AppCompatActivity {

    private Button SearchBtn;
    private EditText inputText;
    private RecyclerView searchlist;
    private String SearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        inputText = findViewById(R.id.search_product_name);
        SearchBtn = findViewById(R.id.search_product_btn);
        searchlist = findViewById(R.id.list_search);
        TextView back = findViewById(R.id.close_settings_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProDuctActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
        searchlist.setLayoutManager(new LinearLayoutManager(EditProDuctActivity.this));
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchInput = inputText.getText().toString();
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(SearchInput), Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =

                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getCategory());
                        holder.txtProductPricegoc.setVisibility(View.INVISIBLE);
                        Glide.with(EditProDuctActivity.this).load(model.getImage()).into(holder.imageView);
                        holder.txtProductPrice.setText("Giá: " + PriceSplit.Nam(model.getPrice()) +" VNĐ");
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                {
                                    CharSequence option[] = new CharSequence[]{
                                            "Xem chi tiết",
                                            "Thêm Ảnh Cho Sản Phẩm",
                                            "Chỉnh Sửa Ảnh Sản Phẩm"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProDuctActivity.this);
                                    builder.setTitle("Tùy chọn");
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, final int i) {
                                            if (i == 0) {
                                                Intent intent = new Intent(EditProDuctActivity.this, AdminMaintainProductActivity.class);
                                                intent.putExtra("pid", model.getPid());
                                                intent.putExtra("category", model.getCategory());
                                                startActivity(intent);
                                            }
                                            if (i == 1) {
                                                Intent intent = new Intent(EditProDuctActivity.this, AddImageProduct.class);
                                                intent.putExtra("pid", model.getPid());
                                                startActivity(intent);
                                            }
                                            if (i == 2) {
                                                Intent intent = new Intent(EditProDuctActivity.this, ShowImageProduct.class);
                                                intent.putExtra("pid", model.getPid());
                                                startActivity(intent);
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
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        searchlist.setAdapter(adapter);
        adapter.startListening();

    }
}