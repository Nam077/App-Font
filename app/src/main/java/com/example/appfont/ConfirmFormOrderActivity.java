package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appfont.Model.Cart;
import com.example.appfont.Model.PriceSplit;
import com.example.appfont.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFormOrderActivity extends AppCompatActivity {
    private EditText spTotalPrice, nameEditText, phoneEdit, emailEdit, addressEdit;
    private Button confirmButton;
    private ImageView back_bt;
   private String saveCurrentTime = null;
    String saveCurrentDate;
    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_form);
        totalAmount = getIntent().getStringExtra("total");
        confirmButton = (Button) findViewById(R.id.shippment_button);
        nameEditText = (EditText) findViewById(R.id.shippment_name);
        back_bt = findViewById(R.id.back_bt_cf);
        spTotalPrice = findViewById(R.id.shippment_totalprice);
        phoneEdit = (EditText) findViewById(R.id.shippment_phone_number);
        emailEdit = (EditText) findViewById(R.id.shippment_email);
        addressEdit = (EditText) findViewById(R.id.shippment_address);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmFormOrderActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });


    }
    private void Check() {
        String a, b, c, d, e, g;
        a = nameEditText.getText().toString();
        b = emailEdit.getText().toString();
        c = phoneEdit.getText().toString();
        d = addressEdit.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (TextUtils.isEmpty(a)) {
            Toast.makeText(this, "Vui lòng nhập tên của bạn", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(b)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(c)) {
            Toast.makeText(this, "Vui lòng nhập Số Điện Thoại", Toast.LENGTH_SHORT).show();
        } else if (!emailEdit.getText().toString().matches(emailPattern)) {
            emailEdit.setError("Nhập email dạng name@example.com");
            return;
        } else if (TextUtils.isEmpty(d)) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
        } else {
            ConfirmOder();
        }
    }

    private void ConfirmOder() {
        addProduct();
        String idOrder = saveCurrentDate+saveCurrentTime+Prevalent.currentOnlineUser.getPhone();
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(idOrder);
        final HashMap<String, Object> orderMap = new HashMap<>();
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        orderMap.put("id",idOrder);
        orderMap.put("user", Prevalent.currentOnlineUser.getPhone());
        orderMap.put("totalAmount", totalAmount);
        orderMap.put("name", nameEditText.getText().toString());
        orderMap.put("email", emailEdit.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("phone", phoneEdit.getText().toString());
        orderMap.put("address", addressEdit.getText().toString());
        orderMap.put("state", "Not Shipped");
        ordersRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseDatabase.getInstance().getReference().child("Cart List")
                        .child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User Order").child(Prevalent.currentOnlineUser.getPhone());
                                    HashMap<String, Object> orderUser = new HashMap<>();
                                    orderUser.put("id", idOrder);
                                    databaseReference.child(idOrder).updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull  Task<Void> task) {
                                            Toast.makeText(ConfirmFormOrderActivity.this, "Hoàn Thành Đặt Hàng", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ConfirmFormOrderActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
            }

        });

    }

    private void addProduct() {
        DatabaseReference OrdersProduct = FirebaseDatabase.getInstance().getReference().child("Orders Product")
                .child(saveCurrentDate+saveCurrentTime+Prevalent.currentOnlineUser.getPhone()).child("Products");
        DatabaseReference Cartref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                .child(Prevalent.currentOnlineUser.getPhone()).child("Products");
        Cartref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                    final HashMap<String, Object> CartMap = new HashMap<>();
                    Cart cart = singleSnapshot.getValue(Cart.class);
                    final HashMap<String, Object> ProductMap = new HashMap<>();
                    ProductMap.put("pid",cart.getPid());
                    ProductMap.put("pname",cart.getPname());
                    ProductMap.put("price",cart.getPrice());
                    ProductMap.put("date",cart.getDate());
                    ProductMap.put("time",cart.getTime());
                    ProductMap.put("image",cart.getImage());
                    ProductMap.put("category",cart.getCategory());
                    ProductMap.put("quantity",cart.getQuantity());
                    ProductMap.put("discount","");
                    OrdersProduct.child(cart.getPid()).updateChildren(ProductMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull  Task<Void> task) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String ki = String.valueOf(String.valueOf(totalAmount));
        ki = PriceSplit.Nam(ki);
        spTotalPrice.setText("Tổng tiền: " + ki + " đồng");
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("phone").getValue().toString();
                    String phone = dataSnapshot.child("email").getValue().toString();
                    emailEdit.setText(phone);
                    String name = dataSnapshot.child("name").getValue().toString();
                    nameEditText.setText(name);
                    if (dataSnapshot.child("address").exists()) {
                        String address = dataSnapshot.child("address").getValue().toString();
                        addressEdit.setText(address);
                    }
                    if (dataSnapshot.child("sdt").exists()) {
                        String sdt = dataSnapshot.child("sdt").getValue().toString();
                        phoneEdit.setText(sdt);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    }
