package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class AddImageSlide extends AppCompatActivity {
    private Button AddNewImageButton;


    private ImageView imageView;
    private static final int GalleryPick = 1;
    private String imageRandomKey, downloadImageUrl;
    private StorageReference ImagesRef;
    private DatabaseReference  ImagesRefDtb;
    private ProgressDialog loadingBar;
    private ImageView InputImage;
    private Uri ImageUri;
    private String Cname, saveCurrentDate, saveCurrentTime, productRandomKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingBar = new ProgressDialog(this);
        setContentView(R.layout.activity_add_image_slide);
        AddNewImageButton = findViewById(R.id.add_new_image);
        InputImage = findViewById(R.id.addImage);
        TextView back = findViewById(R.id.close_settings_btn);
        ImagesRef = FirebaseStorage.getInstance().getReference().child("Slide Images");
        ImagesRefDtb = FirebaseDatabase.getInstance().getReference().child("Image Slide");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddImageSlide.this, SlideImageActivity.class);
                startActivity(intent);
            }
        });
        InputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();

            }
        });
        AddNewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateImageData();
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
            InputImage.setImageURI(ImageUri);
        }
    }
    private void ValidateImageData()
    {

        if (ImageUri == null)
        {
            Toast.makeText(this, "Vui lòng thêm ảnh.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreImageInformation();
        }
    }
    private void StoreImageInformation()
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
        imageRandomKey = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = ImagesRef.child(ImageUri.getLastPathSegment() + imageRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AddImageSlide.this, "Lỗi" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AddImageSlide.this, "Ảnh đã được tải lên thành công", Toast.LENGTH_SHORT).show();

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

                            Toast.makeText(AddImageSlide.this, "Lấy link ảnh thành công", Toast.LENGTH_SHORT).show();

                            SaveImageInfoToDatabase();
                        }
                    }
                });
            }
        });
    }
    private void SaveImageInfoToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("id", imageRandomKey);
        productMap.put("image", downloadImageUrl);
        productRandomKey = saveCurrentDate + saveCurrentTime;
        ImagesRefDtb.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AddImageSlide.this, SlideImageActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AddImageSlide.this, "Thêm thành công..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddImageSlide.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}