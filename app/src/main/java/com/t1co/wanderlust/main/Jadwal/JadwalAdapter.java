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

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.ViewHolder> {
    private final List<Jadwal> jadwalList;
    private final Context context;
    private final OnDetailClickListener onDetailClickListener;

    public interface OnDetailClickListener {
        void onDetailClick(Jadwal jadwal);
    }

    public JadwalAdapter(Context context, List<Jadwal> jadwalList, OnDetailClickListener onDetailClickListener) {
        this.context = context;
        this.jadwalList = jadwalList;
        this.onDetailClickListener = onDetailClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_jadwal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Jadwal jadwal = jadwalList.get(position);

        holder.keberangkatandantujuan.setText(jadwal.getJurusan());
        holder.TanggaldanJam.setText(jadwal.getTanggalJamBerangkat());
        holder.Harga.setText(String.format("%,d IDR", jadwal.getHarga()));
        holder.jumlah_Kursi_tersedia.setText(String.format("%d Kursi Tersedia", jadwal.getJumlahKursiTersedia()));

        holder.btnDetail.setOnClickListener(v -> onDetailClickListener.onDetailClick(jadwal));
    }

    @Override
    public int getItemCount() {
        return jadwalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView keberangkatandantujuan, TanggaldanJam, Harga, jumlah_Kursi_tersedia;
        Button btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            keberangkatandantujuan = itemView.findViewById(R.id.keberangkatand_dan_tujuan);
            TanggaldanJam = itemView.findViewById(R.id.TanggalJam);
            Harga = itemView.findViewById(R.id.Harga);
            jumlah_Kursi_tersedia = itemView.findViewById(R.id.jumlahKursiTersedia);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}
