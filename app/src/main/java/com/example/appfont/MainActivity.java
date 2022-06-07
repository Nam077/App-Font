package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.appfont.Model.Users;
import com.example.appfont.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {
    private Button joinNowButtonn, loginButton;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinNowButtonn =  findViewById(R.id.main_join_now_btn);
        loginButton = findViewById(R.id.main_login_btn);
        loadingBar =  new ProgressDialog(this);
        Paper.init(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        joinNowButtonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UsePasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        if(UserPhoneKey != "" && UsePasswordKey != ""){
            if(!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UsePasswordKey)){
                AllowAcces(UserPhoneKey, UsePasswordKey);
                loadingBar.setTitle("Đang đăng nhập");
                loadingBar.setMessage("Vui lòng chờ");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }
    private void AllowAcces(final String phone, final String password) {
        final DatabaseReference RooRef;
        RooRef = FirebaseDatabase.getInstance().getReference();
        RooRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DialogFragment loadingBar = new DialogFragment();
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Tài khoản ' " + phone+ " 'không tồn tại", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}