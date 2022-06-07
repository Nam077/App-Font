package com.example.appfont;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.appfont.Model.Cart;
import com.example.appfont.Model.Category;
import com.example.appfont.Model.Image;
import com.example.appfont.Model.PriceSplit;
import com.example.appfont.Model.Products;
import com.example.appfont.Prevalent.Prevalent;

import com.example.appfont.ViewHolder.CategoryViewHolder;
import com.example.appfont.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
        private DatabaseReference ProductsRef, CategoryRef, ImageSlideRef;
        private RecyclerView recyclerView , recyclerView2;
        RecyclerView.LayoutManager layoutManager,layoutManager2;
        private  static  final int FRAGMENT_SEARCH = 1;
        private ImageSlider imageSlider;
        private  static  final int FRAGMENT_CATEGORY = 2;
        List<SlideModel> slideModels = new ArrayList<>();
        private  int currenFragment = FRAGMENT_SEARCH;
        @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        ImageSlideRef = FirebaseDatabase.getInstance().getReference().child("Image Slide");
        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
        Paper.init(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.profile_image);
        userNameTextView.setText(Prevalent.currentOnlineUser.getName());
            Glide.with(HomeActivity.this).load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);

        recyclerView2 = findViewById(R.id.list_Category);
        categoriesRecyleview();
        layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2 .setFocusable(false);
        recyclerView2.setNestedScrollingEnabled(false);
        imageSlider = findViewById(R.id.imageslider);
        getListSlideImage();
        recyclerView = findViewById(R.id.recycler_menu);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView .setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull  RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull  RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void categoriesRecyleview() {
        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(CategoryRef, Category.class)
                        .build();
        FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter =
                new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
                    @NonNull
                    @Override
                    public CategoryViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
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
                        Glide.with(HomeActivity.this).load(model.getImage()).into(holder.imageCategory);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HomeActivity.this, ProductViewByCategory.class);
                                intent.putExtra("category", model.getTitle());
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView2.setAdapter(adapter);
        adapter.startListening();
    }
    public  void  getListSlideImage(){
            ImageSlideRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                        Image image = singleSnapshot.getValue(Image.class);
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
    @Override
    protected void onStart() {

        super.onStart();
        categoriesRecyleview();
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef.orderByChild("time"), Products.class)
                        .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getCategory());
                        Integer i = Integer.valueOf(model.getPrice());
                        i = i + ((i/100)*30);
                        String ki = String.valueOf(String.valueOf(model.getPrice()));
                        ki = PriceSplit.Nam(ki);
                        String oi = PriceSplit.Nam(String.valueOf(i));
                        holder.txtProductPrice.setText("Giá: " + ki + " VNĐ");
                        holder.txtProductPricegoc.setText(oi + " đ");
                        Glide.with(HomeActivity.this).load(model.getImage()).into(holder.imageView);
                        holder.txtProductPricegoc.setPaintFlags(holder.txtProductPricegoc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        TextView txtProductName;
                        TextView txtProductDescription;
                        TextView txtProductPrice;
                        TextView txtProductPricegoc;
                        ImageView imageView;
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
                        txtProductName = view.findViewById(R.id.product_name);
                        txtProductDescription = view.findViewById(R.id.product_description);
                        txtProductPrice = view.findViewById(R.id.product_price_goc);
                        txtProductPrice = view.findViewById(R.id.product_price);
                        imageView = view.findViewById(R.id.product_image);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
       recyclerView.setAdapter(adapter);
       adapter.startListening();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_view_order){
            Intent intent = new Intent(HomeActivity.this, ViewOrderActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_cart) {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_oders) {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categories) {
            Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void  replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
}