package com.example.appfont;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appfont.Model.Category;
import com.example.appfont.Model.Dropdow;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class AdminMaintainProductActivity extends AppCompatActivity {
    private String  saveCurrentDate, saveCurrentTime;
    private Button applyChangeBtn,CancelBtn,DeleteBtn;
    private EditText editName, price, description;
    private ImageView imageViewEdit;
    private Uri ImageUri;
    private String ProductID = "";
    private CategoryAdpter categoryAdpter;
    private Spinner spinner;
    private StorageReference ProductImagesRef;
    private ProgressDialog loadingBar;
    private String productRandomKey, downloadImageUrl, ImageProduct;
    private static final int GalleryPick = 1;
    Vector<String> listfontcategory = null;
    private String productImageUrl;
    List<Dropdow> list = new ArrayList<>();
    private DatabaseReference productRef,categoryRef;
    private String ProductCategory,ProductCategoryEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);
        ProductID = getIntent().getStringExtra("pid");
        ProductCategory = getIntent().getStringExtra("category");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(ProductID);
        categoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
        getList();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        applyChangeBtn = findViewById(R.id.edit_product);
        CancelBtn = findViewById(R.id.edit_cancel);
        editName = findViewById(R.id.edit_product_name);
        DeleteBtn = findViewById(R.id.delete_product);
        editName.setMovementMethod(new ScrollingMovementMethod());
        price = findViewById(R.id.edit_product_price);
        spinner = findViewById(R.id.spinner_edit);
        description = findViewById(R.id.edit_product_description);
        description.setMovementMethod(new ScrollingMovementMethod());
        loadingBar = new ProgressDialog(this);
        imageViewEdit = findViewById(R.id.edit_product_image);
        TextView back = findViewById(R.id.close_settings_btn);
        categoryAdpter = new CategoryAdpter(this,R.layout.item_selected,getListCategory());
        spinner.setAdapter(categoryAdpter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ProductCategoryEdit = categoryAdpter.getItem(position).getName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMaintainProductActivity.this, EditProDuctActivity.class);
                startActivity(intent);
            }
        });
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        displaySpecificProductInfo();
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMaintainProductActivity.this, EditProDuctActivity.class);
                startActivity(intent);
            }
        });
        applyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ImageUri == null){
                    SaveProductInfoToDatabase();
                }
                else if (ImageUri != null) {
                    StoreProductInformation();
                }
            }
        });
        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(AdminMaintainProductActivity.this).create();

                alertDialog.setMessage("Bạn có muốn xóa không ?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Có", new       DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletethisproduct();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Không", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

    }

    private void getList() {
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Category category = dataSnapshot.getValue(Category.class);
                    String s = category.getTitle();
                    if(s.equals(ProductCategory) == false){
                        list.add(new Dropdow(s));
                    }

                }
                categoryAdpter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }


    private List<Dropdow> getListCategory() {
        list.add(new Dropdow(ProductCategory));
        return list;
    }

    private void displaySpecificProductInfo() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    int i;
                    String pname = snapshot.child("pname").getValue().toString();
                    String pPrice = snapshot.child("price").getValue().toString();
                    String pdescription = snapshot.child("description").getValue().toString();
                    String pimage = snapshot.child("image").getValue().toString();
                    String pcategoty = snapshot.child("category").getValue().toString();
                    ImageProduct = pimage;
                    editName.setText(pname);
                    price.setText(pPrice);
                    productImageUrl = pimage;
                    description.setText(pdescription);
                    Glide.with(AdminMaintainProductActivity.this).load(pimage).into(imageViewEdit);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void deletethisproduct() {

        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminMaintainProductActivity.this, "Đã Xóa Thành Công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminMaintainProductActivity.this, EditProDuctActivity.class);
                startActivity(intent);
                finish();

            }
        });

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
        productRandomKey = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminMaintainProductActivity.this, "Lỗi" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminMaintainProductActivity.this, "Ảnh đã được tải lên thành công", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(AdminMaintainProductActivity.this, downloadImageUrl, Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }
    private void SaveProductInfoToDatabase() {
        String productid = ProductID;
        String pName = editName.getText().toString();
        String pprice = price.getText().toString();
        String pDescription = description.getText().toString();
        if (TextUtils.isEmpty(pName)) {
            Toast.makeText(this, "Vui lòng nhập Tên Font ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pprice)) {
            Toast.makeText(this, "Vui lòng nhập giá ", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pDescription)) {
            Toast.makeText(this, "Vui lòng nhập mô tả", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productid);
            productMap.put("pname", pName);
            productMap.put("description", pDescription);
            productMap.put("price", pprice);
            if(ImageUri == null){
                Toast.makeText(AdminMaintainProductActivity.this, "Không sửa ảnh", Toast.LENGTH_SHORT).show();
            }
            else  if(ImageUri != null) {
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReferenceFromUrl(productImageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        productMap.put("image", downloadImageUrl);
                    }
                });
            }
            productMap.put("category", ProductCategoryEdit);
            productRef.updateChildren(productMap).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(AdminMaintainProductActivity.this, EditProDuctActivity.class);
                                startActivity(intent);
                                loadingBar.dismiss();
                                Toast.makeText(AdminMaintainProductActivity.this, "Chỉnh sửa thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(AdminMaintainProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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
            imageViewEdit.setImageURI(ImageUri);
        }
    }

}