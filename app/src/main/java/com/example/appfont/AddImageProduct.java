package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appfont.ViewHolder.PhotoAdapter;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class AddImageProduct extends AppCompatActivity {
    private Button button, button2;
    private List<Uri> listUriImage;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private StorageReference ImagesRef;
    private DatabaseReference ImagesRefDtb;
    private ProgressDialog loadingBar;
    private ImageView InputImage;
    private String imageRandomKey, downloadImageUrl;
    private Uri ImageUri;
    private Integer ui = 345;
    private String Cname, saveCurrentDate, saveCurrentTime, productRandomKey,idImage2, ProductID ="", idImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProductID = getIntent().getStringExtra("pid");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image_product);
        ImagesRef = FirebaseStorage.getInstance().getReference().child("Images Products").child(ProductID);
        ImagesRefDtb = FirebaseDatabase.getInstance().getReference().child("Product Images").child(ProductID);
        button = findViewById(R.id.ChoseImage);
        button2 = findViewById(R.id.UploadImage);
        recyclerView = findViewById(R.id.listChoseImage);
        photoAdapter = new PhotoAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setAdapter(photoAdapter);
        loadingBar = new ProgressDialog(this);
        TextView back = findViewById(R.id.close_settings_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddImageProduct.this,ShowImageProduct.class);
                intent.putExtra("pid", ProductID);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateImageData();

            }
        });
        Button done = findViewById(R.id.btn_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddImageProduct.this, ShowImageProduct.class);
                intent.putExtra("pid", ProductID);
                startActivity(intent);
            }
        });
    }
    private void requestPermissions() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openBottomPicker();
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(AddImageProduct.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void openBottomPicker() {
        TedBottomPicker.with(AddImageProduct.this)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Xong")
                .setEmptySelectionText("Chưa CHọn ảnh")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        if (uriList != null) {
                            // here is selected image uri list
                            listUriImage = uriList;
                            photoAdapter.setData(listUriImage);
                        }
                    }
                });
    }
    private void ValidateImageData()
    {

        if ( listUriImage == null)
        {
            Toast.makeText(this, "Vui lòng chọn ảnh.", Toast.LENGTH_SHORT).show();
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
        int i;
        for( i = 0 ; i< listUriImage.size(); i++) {
            ImageUri = listUriImage.get(i);
            idImage2 = imageRandomKey + i ;
            idImage = ImageUri.getLastPathSegment() + imageRandomKey + i +".jpg";
            final StorageReference filePath = ImagesRef;
            final UploadTask uploadTask = filePath.putFile(ImageUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    String message = e.toString();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {

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
                                SaveImageInfoToDatabase();
                            }
                        }
                    });
                }
            });

        }


    }
    private void SaveImageInfoToDatabase()
    {
        ui = ui + 3424;
        String oi = ProductID + ui;
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("id", oi);
        productMap.put("image", downloadImageUrl);

        ImagesRefDtb.child(oi).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(AddImageProduct.this, "Thêm thành công..", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddImageProduct.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}