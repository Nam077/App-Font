package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button CreatAccoutButton;
    private EditText InputName, InputPhoneNumber, InputPassword, InputEmail;
    private ProgressDialog loadingBar;
    private TextView login_btn;
    private String imageprofile = "https://firebasestorage.googleapis.com/" +
            "v0/b/appbanfont.appspot.com/o/Profile%20pictures%2Fprofile_!!.png?" +
            "alt=media&token=52077abb-f02d-44d2-965f-9267a4dea10a";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        CreatAccoutButton = (Button) findViewById(R.id.register_btn);
        InputEmail = (EditText) findViewById(R.id.register_email_input) ;
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        login_btn = findViewById(R.id.rg_login);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        CreatAccoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreatAccout();
            }
        });
    }


    private void CreatAccout() {
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        String email = InputEmail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Vui Lòng Nhập Tên Của Bạn", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Vui Lòng Email", Toast.LENGTH_SHORT).show();
        }
        else if(!email.matches(emailPattern))
        {
            InputEmail.setError("Nhập email dạng name@example.com");
            return;
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Vui lòng nhập tên tài khoản", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Vui Lòng Nhập Mật Khẩu", Toast.LENGTH_SHORT).show();
        }

        else {
            loadingBar.setTitle("Tạo Tài Khoản");
            loadingBar.setMessage("Vui lòng chờ ");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            VailidatephoneNumber(name, phone, password,email);
        }

    }

    private void VailidatephoneNumber(final String name, final String phone, final String password, final String email) {
        final DatabaseReference RooRef;
        RooRef = FirebaseDatabase.getInstance().getReference();
        RooRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(phone).exists())){
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);
                    userdataMap.put("email", email);
                    userdataMap.put("image", imageprofile);
                    RooRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Tài khoản của bạn được tạo thành công", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else {

                                        Toast.makeText(RegisterActivity.this, "Lỗi Mạng vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RegisterActivity.this, "This" + phone + "đã tồn tại", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập lại Số điện thoại", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}