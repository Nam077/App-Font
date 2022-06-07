package com.example.appfont.ViewHolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfont.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>  implements View.OnClickListener{
    private Context mContext;
    private List<Uri> mListPhoto;

    public PhotoAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public  void  setData(List<Uri> list){
        this.mListPhoto = list;
        notifyDataSetChanged();
    }
    public PhotoAdapter(Context mContext, ArrayList<Uri> mListPhoto) {
        this.mContext = mContext;
        this.mListPhoto = mListPhoto;
    }

    @NonNull

    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroup root;
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item_view, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.PhotoViewHolder holder, int position) {
        Uri uri = mListPhoto.get(position);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),uri);
            holder.imagePhoto.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(mListPhoto == null){
            return 0;
        }
        else return mListPhoto.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class  PhotoViewHolder extends RecyclerView.ViewHolder{
        private ImageView imagePhoto;
        public PhotoViewHolder(@NonNull  View itemView) {
            super(itemView);;
            imagePhoto = itemView.findViewById(R.id.image_items);

        }
    }
}