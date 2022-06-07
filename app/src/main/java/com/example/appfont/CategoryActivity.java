package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.appfont.Model.Category;
import com.example.appfont.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CategoryActivity extends AppCompatActivity {
    private DatabaseReference CategoryRef;
    private RecyclerView recyclerView , recyclerView2;
    RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        recyclerView = findViewById(R.id.category_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
        TextView back = findViewById(R.id.close_settings_btn);
    }
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(CategoryRef, Category.class)
                        .build();
        FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
                    @NonNull
                    @Override
                    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        TextView txtCategory;
                        ImageView imageView;
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
                        txtCategory = view.findViewById(R.id.title_category);
                        imageView = view.findViewById(R.id.image_category);
                        CategoryViewHolder holder = new CategoryViewHolder(view);
                        return holder;
                    }
                    @Override
                    protected void onBindViewHolder(@NonNull  CategoryViewHolder holder, int position, @NonNull  Category model) {
                        holder.txtCatregory.setText(model.getTitle());

                        Glide.with(CategoryActivity.this).load(model.getImage()).into(holder.imageCategory);
                        Glide.with(CategoryActivity.this).load(model.getImage()).into(holder.imageCategory);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(CategoryActivity.this, ProductViewByCategory.class);
                                intent.putExtra("category", model.getTitle());
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


        //
    }
}