package com.nvchung.filemanager_ftp_da3.list_utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nvchung.filemanager_ftp_da3.R;

class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView time, date, title;

    MyViewHolder(@NonNull View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.date);
        time = itemView.findViewById(R.id.time);
        title = itemView.findViewById(R.id.title);
        imageView = itemView.findViewById(R.id.icon_file);
    }
}
