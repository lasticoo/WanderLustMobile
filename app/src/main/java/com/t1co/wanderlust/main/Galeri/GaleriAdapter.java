package com.t1co.wanderlust.main.Galeri;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.t1co.wanderlust.R;

import java.util.List;

public class GaleriAdapter extends RecyclerView.Adapter<GaleriAdapter.HistoryViewHolder> {

    private final Context context;
    private final List<GaleriItem> galeriItemList;

    public GaleriAdapter(Context context, List<GaleriItem> galeriItemList) {
        this.context = context;
        this.galeriItemList = galeriItemList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_histoty_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        GaleriItem item = galeriItemList.get(position);

        // Load image using Glide
        Glide.with(context)
                .load(item.getNama_foto())
                .placeholder(new ColorDrawable(Color.LTGRAY)) // Warna placeholder
                .error(new ColorDrawable(Color.RED)) // Warna error
                .into(holder.itemImage);

        // Set keterangan foto
        holder.itemText.setText(item.getKeterangan_foto());
    }

    @Override
    public int getItemCount() {
        return galeriItemList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemText = itemView.findViewById(R.id.item_text);
        }
    }
}
