package com.example.appfont;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.appfont.Model.AdminOrders;

public class AdminActivity extends AppCompatActivity {
    private CardView addCategory, viewCatagory, viewOrder, Logout,SlideImage ,AdminAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin);
        addCategory =  findViewById(R.id.add_Category);
        AdminAddCategory = findViewById(R.id.addCategory);
        viewCatagory =  findViewById(R.id.view_Category);
        Logout =  findViewById(R.id.admin_logout);
        SlideImage = findViewById(R.id.viewImage);
        viewOrder = findViewById(R.id.viewOder);
        SlideImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, SlideImageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AdminAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminAddCatergoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminViewOrderActivity.class);
                startActivity(intent);
                finish();
            }
        });
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminCategoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        viewCatagory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, EditProDuctActivity.class);
                startActivity(intent);
//                Intent intent = new Intent(AdminActivity.this, HomeActivity.class);
////                intent.putExtra("Admin","Admin");
//                startActivity(intent);
//                finish();
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        viewOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AdminActivity.this, AdminViewOrderActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }
    public static class HomeActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
        }
    }
}