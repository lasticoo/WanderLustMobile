package com.t1co.wanderlust.main.Pemesanan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PemesananAdapter extends ArrayAdapter<Pemesanan> {
    private Context context;
    private List<Pemesanan> pemesananList;

    public PemesananAdapter(Context context, List<Pemesanan> pemesananList) {
        super(context, 0, pemesananList);
        this.context = context;
        this.pemesananList = pemesananList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Mendapatkan data pemesanan untuk posisi saat ini
        Pemesanan pemesanan = getItem(position);

        // Memeriksa apakah tampilan sudah ada, jika tidak, inflate layout baru
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        // Mengambil tampilan untuk menampilkan data
        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        // Menampilkan data pemesanan
        text1.setText("ID Jadwal: " + pemesanan.getId_jadwal() + ", No Kursi: " + pemesanan.getNo_kursi());
        text2.setText("Status: " + pemesanan.getStatus() + ", Email: " + pemesanan.getEmail());

        // Mengembalikan tampilan yang telah diisi dengan data
        return convertView;
    }
}