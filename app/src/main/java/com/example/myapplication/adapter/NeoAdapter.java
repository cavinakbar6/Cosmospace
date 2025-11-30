package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.NearEarthObject;
import java.util.List;
import java.util.Locale;

public class NeoAdapter extends RecyclerView.Adapter<NeoAdapter.NeoViewHolder> {

    private List<NearEarthObject> neoList;
    private Context context;
    private static final double DISTANCE_THRESHOLD = 10000000.0;

    // --- 1. BUAT INTERFACE LISTENER ---
    public interface OnNeoClickListener {
        void onNeoClick(NearEarthObject neo);
    }

    private OnNeoClickListener listener;

    // Tambahkan listener ke Constructor
    public NeoAdapter(Context context, List<NearEarthObject> neoList, OnNeoClickListener listener) {
        this.context = context;
        this.neoList = neoList;
        this.listener = listener;
    }
    // ----------------------------------

    @NonNull
    @Override
    public NeoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_neo, parent, false);
        return new NeoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NeoViewHolder holder, int position) {
        NearEarthObject neo = neoList.get(position);

        holder.textNeoName.setText(neo.getName());
        double diameter = neo.getEstimatedDiameter().getMeters().getEstimatedDiameterMax();
        holder.textNeoSize.setText(String.format(Locale.getDefault(), "Size: %.1f m", diameter));

        if (!neo.getCloseApproachData().isEmpty()) {
            String velocity = neo.getCloseApproachData().get(0).getRelativeVelocity().getKilometersPerHour();
            String distanceStr = neo.getCloseApproachData().get(0).getMissDistance().getKilometers();

            double velocityKmH = Double.parseDouble(velocity);
            double distanceKm = Double.parseDouble(distanceStr);

            holder.textNeoVelocity.setText(String.format(Locale.getDefault(), "Speed: %,.0f km/h", velocityKmH));
            holder.textNeoDistance.setText(String.format(Locale.getDefault(), "Jarak: %,.0f km", distanceKm));

            if (distanceKm < DISTANCE_THRESHOLD) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#5A0000"));
                holder.textStatus.setText("⚠️ WASPADA");
                holder.textStatus.setTextColor(Color.parseColor("#FF5252"));
            } else {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#004D40"));
                holder.textStatus.setText("✅ AMAN");
                holder.textStatus.setTextColor(Color.parseColor("#69F0AE"));
            }
        }

        // --- 2. PASANG CLICK LISTENER ---
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNeoClick(neo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return neoList.size();
    }

    static class NeoViewHolder extends RecyclerView.ViewHolder {
        TextView textNeoName, textNeoSize, textNeoVelocity, textNeoDistance, textStatus;
        CardView cardView;

        public NeoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNeoName = itemView.findViewById(R.id.text_neo_name);
            textNeoSize = itemView.findViewById(R.id.text_neo_size);
            textNeoVelocity = itemView.findViewById(R.id.text_neo_velocity);
            textNeoDistance = itemView.findViewById(R.id.text_neo_distance);
            textStatus = itemView.findViewById(R.id.text_neo_status);
            cardView = itemView.findViewById(R.id.card_neo_container);
        }
    }
}