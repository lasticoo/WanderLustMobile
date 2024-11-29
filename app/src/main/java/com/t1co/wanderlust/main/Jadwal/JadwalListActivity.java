package com.t1co.wanderlust.main.Jadwal;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.Pemesanan.PemesananPageActivity;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JadwalListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private JadwalAdapter adapter;
    private List<Jadwal> jadwalList;
    private SharedPreferences sharedPreferences;
    private VolleyHandler volleyHandler;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        volleyHandler = VolleyHandler.getInstance(this);

        Intent intent = getIntent();
        String fromLocation = intent.getStringExtra("from_location");
        String toLocation = intent.getStringExtra("to_location");
        String travelDate = intent.getStringExtra("travel_date");

        loadJadwalData(fromLocation, toLocation, travelDate);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadJadwalData(fromLocation, toLocation, travelDate);
        });
    }

    private void loadJadwalData(String fromLocation, String toLocation, String travelDate) {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        jadwalList = new ArrayList<>();

        JSONObject params = new JSONObject();
        try {
            params.put("keberangkatan", fromLocation);
            params.put("tujuan", toLocation);
            params.put("caritgl", travelDate);
        } catch (JSONException e) {
            e.printStackTrace();
            showCustomErrorDialog("Error", "Terjadi kesalahan saat memproses data");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        String token = sharedPreferences.getString("token", "");
        String userId = sharedPreferences.getString("id_user", "");

        if (token.isEmpty()) {
            showCustomErrorDialog("Error", "Token tidak ditemukan");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("Content-Type", "application/json");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiConfig.CARIJADWAL_URL, params,
                response -> {
                    swipeRefreshLayout.setRefreshing(false);
                    try {
                        String status = response.getString("status");
                        String message = response.getString("message");

                        if (status.equals("sukses")) {
                            // Cek apakah data kosong
                            if (!response.has("data") || response.isNull("data")) {
                                showCustomErrorDialog("Tidak Ada Jadwal", "Tidak ada jadwal yang tersedia untuk pencarian ini");
                                return;
                            }

                            JSONArray dataArray = response.getJSONArray("data");
                            if (dataArray.length() == 0) {
                                showCustomErrorDialog("Tidak Ada Jadwal", "Tidak ada jadwal yang tersedia untuk pencarian ini");
                                return;
                            }

                            // Sisanya tetap sama dengan kode sebelumnya
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String idJadwal = dataObject.getString("id_jadwal");
                                String jurusan = dataObject.getString("keberangkatan") + " - " + dataObject.getString("tujuan");
                                String tanggalJamBerangkat = dataObject.getString("tanggal") + " " + dataObject.getString("jam");
                                int harga = dataObject.getInt("harga");
                                int jumlahKursiTersedia = dataObject.getInt("jumlah_kursi_tersedia");

                                Jadwal jadwal = new Jadwal(idJadwal, jurusan, tanggalJamBerangkat, harga, jumlahKursiTersedia);
                                jadwalList.add(jadwal);
                            }

                            final String idUser = sharedPreferences.getString("id_user", "");

                            adapter = new JadwalAdapter(JadwalListActivity.this, jadwalList, jadwal -> navigateToDetail(jadwal, idUser));
                            recyclerView.setAdapter(adapter);

                        } else {
                            showCustomErrorDialog("Pencarian Gagal", message);
                        }
                    } catch (JSONException e) {
                        showCustomErrorDialog("Error", "Terjadi kesalahan saat memproses data");
                        Log.e("JadwalListActivity", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    swipeRefreshLayout.setRefreshing(false);
                    String errorMessage = "Terjadi kesalahan pada server";

                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 404) {
                            errorMessage = "Tidak ada jadwal yang ditemukan";
                        } else if (error.networkResponse.statusCode == 401) {
                            errorMessage = "Token tidak valid";
                        }
                    }

                    showCustomErrorDialog("Error", errorMessage);
                    Log.e("JadwalListActivity", "Volley Error: " + error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        // Sisanya tetap sama
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void showCustomErrorDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        dialog.getWindow().setAttributes(layoutParams);

        ImageView iconView = dialog.findViewById(R.id.dialogIcon);
        TextView titleView = dialog.findViewById(R.id.dialogTitle);
        TextView messageView = dialog.findViewById(R.id.dialogMessage);
        Button okButton = dialog.findViewById(R.id.dialogButton);

        iconView.setImageResource(R.drawable.ic_error);
        titleView.setText(title);
        messageView.setText(message);
        okButton.setOnClickListener(v -> {
            dialog.dismiss();

            // Kondisi untuk kembali ke activity sebelumnya pada error tertentu
            if (title.equals("Tidak Ada Jadwal") ||
                    title.equals("Pencarian Gagal") ||
                    title.equals("Error") ||
                    title.equals("Token tidak ditemukan")) {
                onBackPressed(); // Kembali ke activity sebelumnya
            }
        });

        dialog.show();
    }

    private void navigateToDetail(Jadwal jadwal, String userId) {
        if (jadwal.getJumlahKursiTersedia() <= 0) {
            showCustomErrorDialog("Kursi Penuh", "Maaf, kursi untuk jadwal ini sudah penuh. Silakan pilih jadwal lain.");
            return;
        }

        Intent intent = new Intent(this, PemesananPageActivity.class);
        intent.putExtra("id_jadwal", jadwal.getIdJadwal());
        intent.putExtra("id_user", userId);
        intent.putExtra("jurusan", jadwal.getJurusan());
        intent.putExtra("tanggal_jam", jadwal.getTanggalJamBerangkat());
        intent.putExtra("harga", jadwal.getHarga());
        intent.putExtra("jumlah_kursi", jadwal.getJumlahKursiTersedia());
        startActivity(intent);
    }}