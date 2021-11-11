package com.example.drawableapp;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<String> imageList;
    private ArrayList<String> nameList;

    public ImageAdapter(ArrayList<String> imageList,ArrayList<String> nameList, Context context) {
        this.imageList = imageList;
        this.nameList = nameList;
        this.context = context;
    }

    interface ListItemClickListener{
        void onListItemClick(int position);
    }

    private Context context;
    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        // loading the images from the position
        holder.button.setText(nameList.get(position));
        Glide.with(holder.itemView.getContext()).load(imageList.get(position)).into(holder.imageView);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, DrawActivity.class);
                holder.imageView.setDrawingCacheEnabled(true);
                holder.imageView.buildDrawingCache();
                Bitmap b = holder.imageView.getDrawingCache(); // your bitmap
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bs);
                intent.putExtra("byteArray", bs.toByteArray());
                intent.putExtra("IMAGE_URL", imageList.get(position));
                intent.putExtra("IMAGE_NAME", nameList.get(position));
                intent.putExtra("IMAGE_NAME", nameList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        ImageView imageView;
        Button button;
        //TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.item_image);
            button=itemView.findViewById(R.id.item_name);
        }
    }
}


