package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.data.FeatureItem;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder> {

    private List<FeatureItem> featureList;
    private OnFeatureClickListener listener;

    public interface OnFeatureClickListener {
        void onFeatureClick(int fragmentId);
    }

    public FeatureAdapter(List<FeatureItem> featureList, OnFeatureClickListener listener) {
        this.featureList = featureList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        FeatureItem item = featureList.get(position);
        holder.title.setText(item.getTitle());
        holder.desc.setText(item.getDescription());

        holder.bgImage.setImageResource(item.getImageResId());

        holder.itemView.setOnClickListener(v -> listener.onFeatureClick(item.getTargetFragmentId()));
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    static class FeatureViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc;
        ImageView bgImage;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_feature_title);
            desc = itemView.findViewById(R.id.text_feature_desc);
            bgImage = itemView.findViewById(R.id.img_feature_bg);
        }
    }
}