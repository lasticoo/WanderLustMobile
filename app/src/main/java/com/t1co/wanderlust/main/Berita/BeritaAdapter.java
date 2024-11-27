package com.t1co.wanderlust.main.Berita;

import android.content.Context;
import android.text.Html;
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

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder> {

    private final List<BeritaModel> beritaList;
    private final Context context;

    public BeritaAdapter(Context context, List<BeritaModel> beritaList) {
        this.context = context;
        this.beritaList = beritaList;
    }

    @NonNull
    @Override
    public BeritaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_berita, parent, false);
        return new BeritaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BeritaViewHolder holder, int position) {
        BeritaModel berita = beritaList.get(position);

        holder.judulBerita.setText(berita.getJudulBerita());

        holder.tglBerita.setText(berita.getTglBerita());

        String plainText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            plainText = Html.fromHtml(berita.getKontenBerita(), Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            plainText = Html.fromHtml(berita.getKontenBerita()).toString();
        }
        holder.kontenBerita.setText(plainText);

        Glide.with(context)
                .load(berita.getFotoBerita())
                .into(holder.fotoBerita);
    }

    @Override
    public int getItemCount() {
        return beritaList.size();
    }

    public static class BeritaViewHolder extends RecyclerView.ViewHolder {
        TextView judulBerita;
        TextView tglBerita;
        TextView kontenBerita;
        ImageView fotoBerita;

        public BeritaViewHolder(@NonNull View itemView) {
            super(itemView);
            judulBerita = itemView.findViewById(R.id.judul_berita);
            tglBerita = itemView.findViewById(R.id.tgl_berita);
            kontenBerita = itemView.findViewById(R.id.konten_berita);
            fotoBerita = itemView.findViewById(R.id.foto_berita);
        }
    }
}