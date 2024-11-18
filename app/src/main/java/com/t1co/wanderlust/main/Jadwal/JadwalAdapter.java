package com.t1co.wanderlust.main.Jadwal;

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

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder> {
    private List<Jadwal> jadwalList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Jadwal jadwal);
    }

    public JadwalAdapter(Context context, List<Jadwal> jadwalList, OnItemClickListener listener) {
        this.context = context;
        this.jadwalList = jadwalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ubah ini dari activity_jadwal_list menjadi activity_jadwal_item
        View view = LayoutInflater.from(context).inflate(R.layout.activity_jadwal_item, parent, false);
        return new JadwalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalViewHolder holder, int position) {
        Jadwal jadwal = jadwalList.get(position);

        holder.tvBusName.setText(jadwal.getBusName());
        holder.tvBusType.setText(jadwal.getBusType());
        holder.tvPrice.setText(String.format("Rp %,.0f", jadwal.getPrice()));
        holder.tvDepartureTime.setText(jadwal.getDepartureTime());
        holder.tvArrivalTime.setText(jadwal.getArrivalTime());
        holder.tvSeatsAvailable.setText(jadwal.getSeatsAvailable() + " Kursi Tersedia");

        holder.btnBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(jadwal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jadwalList != null ? jadwalList.size() : 0;
    }

    public static class JadwalViewHolder extends RecyclerView.ViewHolder {
        TextView tvBusName, tvBusType, tvPrice, tvDepartureTime, tvArrivalTime, tvSeatsAvailable;
        Button btnBook;

        public JadwalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBusName = itemView.findViewById(R.id.tvBusName);
            tvBusType = itemView.findViewById(R.id.tvBusType);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvArrivalTime = itemView.findViewById(R.id.tvArrivalTime);
            tvSeatsAvailable = itemView.findViewById(R.id.tvSeatsAvailable);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }

    public void updateData(List<Jadwal> newList) {
        jadwalList = newList;
        notifyDataSetChanged();
    }
}