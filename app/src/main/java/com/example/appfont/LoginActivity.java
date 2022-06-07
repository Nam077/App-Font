package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appfont.Model.Users;
import com.example.appfont.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton ;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;
    private TextView AdminLink, NotAdminLink;
    private TextView rg_btn;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        rg_btn = (TextView) findViewById(R.id.lg_register);
        loadingBar = new ProgressDialog(this);
        chkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxlogin);
        Paper.init(this);

        rg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Đăng Nhập Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                chkBoxRememberMe.setVisibility(View.INVISIBLE);
                parentDbName = "Admins";

            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Đăng Nhập");
                NotAdminLink.setVisibility(View.INVISIBLE);
                AdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Users";
                chkBoxRememberMe.setVisibility(View.VISIBLE);
                chkBoxRememberMe.setChecked(false);
            }
        });

    }

    private void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Vui Lòng Nhập Số Điện Thoại", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Vui Lòng Nhập Mật Khẩu", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Đang kiểm tra");
            loadingBar.setMessage("Vui lòng chờ ");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccout(phone, password);
        }
    }

    private void AllowAccessToAccout(final String phone, final String password)
    {

        if(chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);

        }
        final DatabaseReference RooRef;
        RooRef = FirebaseDatabase.getInstance().getReference();
        RooRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){
                            if(parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Xin chào Admin", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users")){
                                Prevalent.currentOnlineUser = usersData;
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);

                            }

                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Tài khoản ' " + phone+ " 'không tồn tại", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}