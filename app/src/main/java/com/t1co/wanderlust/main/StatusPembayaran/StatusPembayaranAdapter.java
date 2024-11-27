package com.t1co.wanderlust.main.StatusPembayaran;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.t1co.wanderlust.R;

import java.util.List;

public class StatusPembayaranAdapter extends RecyclerView.Adapter<StatusPembayaranAdapter.ViewHolder> {

    private List<StatusPembayaranModel> statusList;
    private Context context;

    public StatusPembayaranAdapter(List<StatusPembayaranModel> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status_pembayaran, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set data ke item
        StatusPembayaranModel item = statusList.get(position);
        holder.tvIdPesan.setText(item.getIdPesan());
        holder.tvTujuan.setText(item.getTujuan());
        holder.tvWaktu.setText(item.getWaktu());
        holder.tvHarga.setText(item.getHarga());
        holder.tvStatus.setText(item.getStatus());

        // Atur margin top hanya untuk item pertama
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (position == 0) {
            layoutParams.setMargins(0, 100, 0, 8); // Margin top 100dp untuk item pertama
        } else {
            layoutParams.setMargins(0, 0, 0, 8); // Tidak ada margin top untuk item lainnya
        }
        holder.itemView.setLayoutParams(layoutParams);
    }


    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvIdPesan, tvTujuan, tvWaktu, tvHarga, tvStatus;
        Button btnBatal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIdPesan = itemView.findViewById(R.id.tvIdPesan);
            tvTujuan = itemView.findViewById(R.id.tvTujuan);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnBatal = itemView.findViewById(R.id.btnBatal);
        }
    }
}
