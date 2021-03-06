package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appfont.Model.Image;
import com.example.appfont.ViewHolder.ImageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ShowImageProduct extends AppCompatActivity {
    private DatabaseReference ImageRef;
    private RecyclerView recyclerView;
    private TextView buttonAdd;
    private String ProductID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_product);
        ProductID = getIntent().getStringExtra("pid");
        ImageRef = FirebaseDatabase.getInstance().getReference().child("Product Images").child(ProductID);
        recyclerView = findViewById(R.id.list_image);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        buttonAdd = findViewById(R.id.addImageProduct);
        recyclerView.setLayoutManager(layoutManager);
        TextView back = findViewById(R.id.close_settings_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowImageProduct.this,EditProDuctActivity.class);
                startActivity(intent);
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowImageProduct.this,AddImageProduct.class);
                intent.putExtra("pid", ProductID);
                startActivity(intent);
                finish();
            }
        });
    }
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Image> options =
                new FirebaseRecyclerOptions.Builder<Image>()
                        .setQuery(ImageRef, Image.class)
                        .build();

        FirebaseRecyclerAdapter<Image, ImageViewHolder> adapter =
                new FirebaseRecyclerAdapter<Image, ImageViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ImageViewHolder holder, int position, @NonNull final Image model) {
                        Glide.with(ShowImageProduct.this).load(model.getImage()).into(holder.imageView);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                {
                                    CharSequence option[] = new CharSequence[]{
                                            "Xem ???nh",
                                            "X??a ???nh"
                                    };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowImageProduct.this);
                                    builder.setTitle("T??y ch???n");
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, final int i) {
                                            if (i == 0) {
//                                                Intent intent = new Intent(SlideImageActivity.this, ProductDetailsActivity.class);
//                                                intent.putExtra("pid", model.getId());
//                                                startActivity(intent);
                                            }
                                            if (i == 1) {
                                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(model.getImage());
                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        DatabaseReference ImageRef2 = FirebaseDatabase.getInstance().getReference().child("Product Images").child(ProductID);
                                                        ImageRef2.child(model.getId())
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(ShowImageProduct.this, "X??a Th??nh C??ng", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(ShowImageProduct.this, ShowImageProduct.class);
                                                                        intent.putExtra("pid", ProductID);
                                                                        startActivity(intent);
                                                                    }
                                                                });
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
                    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        ImageView imageView;
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_view, parent, false);
                        imageView = view.findViewById(R.id.image_items);
                        ImageViewHolder holder = new ImageViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}