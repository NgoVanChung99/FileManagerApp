package com.nvchung.filemanager_ftp_da3.grid_utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nvchung.filemanager_ftp_da3.R;

class GridViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    ImageView imageView;

    public GridViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text_grid);
        imageView = itemView.findViewById(R.id.icon_grid);
    }
}
