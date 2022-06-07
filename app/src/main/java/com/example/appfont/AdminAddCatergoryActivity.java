package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appfont.Model.Category;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddCatergoryActivity extends AppCompatActivity {
    private EditText InputCategoryName;
    private Button addCategory;
    private Uri ImageUri;
    private String Cname, saveCurrentDate, saveCurrentTime, productRandomKey;
    private ImageView imageView;
    private static final int GalleryPick = 1;
    private String categoryRandomKey, downloadImageUrl;
    private StorageReference CategoryImagesRef;
    private DatabaseReference  CategoryRef;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_add_catergory);
        InputCategoryName = findViewById(R.id.category_name);
        CategoryImagesRef = FirebaseStorage.getInstance().getReference().child("Category Images");
        CategoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
        addCategory = findViewById(R.id.add_new_category);
        imageView = findViewById(R.id.select_category_image);
        loadingBar = new ProgressDialog(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateProductData();
            }
        });
        TextView back = findViewById(R.id.close_settings_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminAddCatergoryActivity.this,AdminActivity.class);
                startActivity(intent);
            }
        });
    }
    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            imageView.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData()
    {
        Cname = InputCategoryName.getText().toString();
        if (ImageUri == null)
        {
            Toast.makeText(this, "Vui lòng thêm ảnh.", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(Cname))
        {
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }
    private void StoreProductInformation()
    {
        loadingBar.setTitle("Đang thêm");
        loadingBar.setMessage("Xin vui lòng chờ ");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        categoryRandomKey = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = CategoryImagesRef.child(ImageUri.getLastPathSegment() + categoryRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminAddCatergoryActivity.this, "Lỗi" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminAddCatergoryActivity.this, "Ảnh đã được tải lên thành công", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddCatergoryActivity.this, "Lấy link ảnh thành công", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }
    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("id", categoryRandomKey);
        productMap.put("image", downloadImageUrl);
        productRandomKey = saveCurrentDate + saveCurrentTime;
        productMap.put("title", Cname);
        CategoryRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminAddCatergoryActivity.this, AdminAddCatergoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddCatergoryActivity.this, "Thêm thành công..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddCatergoryActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}