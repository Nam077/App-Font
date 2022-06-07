package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.appfont.Model.Image;
import com.example.appfont.Model.PriceSplit;
import com.example.appfont.Model.Products;
import com.example.appfont.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    private Button addToCartBtn, closeBtn;
    private ImageView productImage;
    private TextView numberButton;
    List<SlideModel> slideModels = new ArrayList<>();
    private DatabaseReference ImageSlideRef;

    private ImageSlider imageSlider;
    private ImageButton back;
    private TextView productPrice, productDescription, productName,productCategoris;
    private String ProductID = "";
    private String  getPrice = "";
    private String Image,Category;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ProductID = getIntent().getStringExtra("pid");
        ImageSlideRef = FirebaseDatabase.getInstance().getReference().child("Product Images").child(ProductID);
        addToCartBtn = (Button) findViewById(R.id.add_to_cart);
        numberButton = (TextView) findViewById(R.id.number_btn);
        back = findViewById(R.id.back_bt_prd);
        imageSlider = findViewById(R.id.imageslider_2);

        closeBtn     = (Button) findViewById(R.id.close_btn);
        productCategoris = (TextView) findViewById(R.id.product_category_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productDescription.setMovementMethod(new ScrollingMovementMethod());
        productPrice= (TextView) findViewById(R.id.product_price_details);
        getProDuctDetails(ProductID);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToCartList();

            }

            private void addingToCartList() {
                String saveCurrentTime = null, saveCurrentDate;
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                saveCurrentDate = currentDate.format(calendar.getTime());
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime = currentTime.format(calendar.getTime());
                final DatabaseReference cartListref = FirebaseDatabase.getInstance().getReference().child("Cart List");
                final HashMap<String, Object> cartMap = new HashMap<>();
                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
                cartMap.put("pid",ProductID);
                cartMap.put("pname",productName.getText().toString());
                cartMap.put("price",getPrice);
                cartMap.put("date",saveCurrentDate);
                cartMap.put("time",saveCurrentTime);
                cartMap.put("image",Image);
                cartMap.put("category", Category);
                cartMap.put("quantity",numberButton.getText().toString());
                cartMap.put("discount","");
                cartListref.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products").child(ProductID)
                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                                        Toast.makeText(ProductDetailsActivity.this, "Thêm vào giỏ thành công", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                        startActivity(intent);
                                    }
                    }
                });



            }
        });


    }

    private void getListSlideImage() {
        ImageSlideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    com.example.appfont.Model.Image image = singleSnapshot.getValue(Image.class);
                    String s = image.getImage();
                    slideModels.add(new SlideModel(s));
                }
                imageSlider.setImageList(slideModels,true);
            }
            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void getProDuctDetails(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(ProductID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String k,u;
                    Products product = snapshot.getValue(Products.class);
                    productName.setText(product.getPname());
                    String ki = String.valueOf(String.valueOf(product.getPrice()));
                    ki = PriceSplit.Nam(ki);
                    productPrice.setText("Giá: "+ ki +" đồng");
                    getPrice = product.getPrice();
                    Image = product.getImage();
                    Category = product.getCategory();
                    productCategoris.setText(product.getCategory());
                    productDescription.setText(product.getDescription());
                    slideModels.add(new SlideModel(product.getImage()));
                    getListSlideImage();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}