package com.t1co.wanderlust.main.Pembayaran;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.VolleyError;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PembayaranPageActivity extends AppCompatActivity {
    private static final String TAG = "PembayaranPageActivity";

    private TextView txtIdPesan, txtTanggalKeberangkatan, txtKeberangkatanTujuan, txtTotalPembayaran, txtStatusBayar;
    private Button btnKonfirmasiPembayaran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_page);

        // Inisialisasi TextView
        txtIdPesan = findViewById(R.id.idpesan);
        txtTanggalKeberangkatan = findViewById(R.id.tglkeberangkatan);
        txtKeberangkatanTujuan = findViewById(R.id.keberangkatand_dan_tujuan1);
        txtTotalPembayaran = findViewById(R.id.totalpembayaran);
        txtStatusBayar = findViewById(R.id.statusbayar);
        btnKonfirmasiPembayaran = findViewById(R.id.button_confirm);

        // Ambil ID Pesan dari Intent sebagai String
        Intent intent = getIntent();
        String idPesan = intent.getStringExtra("id_pesan"); // Ambil sebagai String

        if (idPesan == null || idPesan.isEmpty()) {
            Toast.makeText(this, "ID Pesan tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tampilkan ID Pesan di TextView
        txtIdPesan.setText(idPesan);

        // Ambil token dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        getDetailPembayaran(idPesan, token);

        btnKonfirmasiPembayaran.setOnClickListener(v -> {
            Intent konfirmasiIntent = new Intent(PembayaranPageActivity.this, KonfirmasiPembayaranActivity.class);
            konfirmasiIntent.putExtra("id_pesan", idPesan);  // idPesan langsung
            startActivity(konfirmasiIntent);
        });
    }

    private void getDetailPembayaran(String idPesan, String token) {
        String url = ApiConfig.PEMBAYARAN_URL + "?id_pesan=" + idPesan;

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token); // Menambahkan token ke header

        VolleyHandler.getInstance(this).makeGetRequestWithHeaders(url, headers, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result); // Menggunakan JSONObject
                    if (jsonResponse.getString("status").equals("sukses")) {
                        JSONObject data = jsonResponse.getJSONObject("data"); // Ambil objek langsung

                        if (data != null) {
                            // Mengisi data ke TextView
                            txtTanggalKeberangkatan.setText(data.getString("tgl_berangkat"));
                            txtKeberangkatanTujuan.setText(data.getString("Keberangkatan") + " - " + data.getString("Tujuan"));
                            txtTotalPembayaran.setText("Rp. " + data.getString("harga"));
                            txtStatusBayar.setText(data.getString("status"));
                        } else {
                            Log.e(TAG, "Data tidak ditemukan.");
                        }
                    } else {
                        Log.e(TAG, "Error: " + jsonResponse.getString("message"));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error: " + error);
            }
        });
    }
}