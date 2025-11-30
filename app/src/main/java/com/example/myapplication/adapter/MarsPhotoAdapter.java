package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.data.MarsPhoto;
import java.util.List;

public class MarsPhotoAdapter extends RecyclerView.Adapter<MarsPhotoAdapter.PhotoViewHolder> {

    private Context context;
    private List<MarsPhoto> photoList;

    public MarsPhotoAdapter(Context context, List<MarsPhoto> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mars_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        MarsPhoto photo = photoList.get(position);

        holder.textRoverInfo.setText("Rover: " + photo.getRover().getName());
        holder.textEarthDate.setText("Date: " + photo.getEarthDate());

        Glide.with(context)
                .load(photo.getImgSrc())
                .placeholder(R.color.cosmic_black_darker) // Warna placeholder saat loading
                .into(holder.imageMars);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMars;
        TextView textRoverInfo, textEarthDate;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMars = itemView.findViewById(R.id.image_mars_photo);
            textRoverInfo = itemView.findViewById(R.id.text_rover_info);
            textEarthDate = itemView.findViewById(R.id.text_earth_date);
        }
    }
}