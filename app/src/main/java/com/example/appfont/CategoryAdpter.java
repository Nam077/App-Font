package com.example.appfont;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appfont.Model.Category;
import com.example.appfont.Model.Dropdow;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdpter extends ArrayAdapter<Dropdow> {
    public CategoryAdpter(@NonNull Context context, int resource, @NonNull List<Dropdow> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected,parent,false);
        TextView tvSelected = convertView.findViewById(R.id.tv_sellected);
        Dropdow dropdow = this.getItem(position);
        if(dropdow!=null) {
            tvSelected.setText(dropdow.getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull  ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_dropdown,parent,false);
        TextView tvCategory = convertView.findViewById(R.id.tv_category);
        Dropdow dropdow = this.getItem(position);
       if(dropdow!=null) {
           tvCategory.setText(dropdow.getName());
       }
       return convertView;
    }
}
