package com.t1co.wanderlust.main.Jadwal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_list);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        volleyHandler = VolleyHandler.getInstance(this);

        Intent intent = getIntent();
        String fromLocation = intent.getStringExtra("from_location");
        String toLocation = intent.getStringExtra("to_location");
        String travelDate = intent.getStringExtra("travel_date");

        loadJadwalData(fromLocation, toLocation, travelDate);
    }

    private void loadJadwalData(String fromLocation, String toLocation, String travelDate) {
        jadwalList = new ArrayList<>();

        JSONObject params = new JSONObject();
        try {
            params.put("keberangkatan", fromLocation);
            params.put("tujuan", toLocation);
            params.put("caritgl", travelDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String token = sharedPreferences.getString("token", "");
        String userId = sharedPreferences.getString("id_user", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        // Debug log untuk memeriksa parameter
        Log.d("JadwalListActivity", "Parameters: " + params.toString());
        Log.d("JadwalListActivity", "Token: " + token);
        Log.d("JadwalListActivity", "User ID: " + userId);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("Content-Type", "application/json");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiConfig.CARIJADWAL_URL, params,                response -> {
                    Log.d("JadwalListActivity", "Response: " + response.toString()); // Debug log untuk response
                    try {
                        if (response.getString("status").equals("sukses")) {
                            JSONArray dataArray = response.getJSONArray("data");

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String idJadwal = dataObject.getString("id_jadwal");
                                String jurusan = dataObject.getString("keberangkatan") + " - " + dataObject.getString("tujuan");
                                String tanggalJamBerangkat = dataObject.getString("tanggal") + " " + dataObject.getString("jam");
                                int harga = dataObject.getInt("harga");
                                int jumlahKursiTersedia = dataObject.getInt("jumlah_kursi_tersedia");

                                // Debug log untuk setiap item jadwal
                                Log.d("JadwalListActivity", "Jadwal Item: ID=" + idJadwal +
                                        ", Jurusan=" + jurusan +
                                        ", TanggalJam=" + tanggalJamBerangkat);

                                Jadwal jadwal = new Jadwal(idJadwal, jurusan, tanggalJamBerangkat, harga, jumlahKursiTersedia);
                                jadwalList.add(jadwal);
                            }

                            adapter = new JadwalAdapter(JadwalListActivity.this, jadwalList, jadwal -> {
                                navigateToDetail(jadwal, userId);
                            });
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(JadwalListActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("JadwalListActivity", "JSON Error: " + e.getMessage()); // Debug log untuk error
                        e.printStackTrace();
                        Toast.makeText(JadwalListActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("JadwalListActivity", "Volley Error: " + error.toString()); // Debug log untuk error
                    if (error.networkResponse != null) {
                        Log.e("JadwalListActivity", "Error Status Code: " + error.networkResponse.statusCode);
                        Log.e("JadwalListActivity", "Error Data: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(JadwalListActivity.this,
                            "Server error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        // Tambahkan timeout yang lebih lama
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 detik timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }



    private void navigateToDetail(Jadwal jadwal, String userId) {
        Intent intent = new Intent(this, PemesananPageActivity.class);
        intent.putExtra("id_jadwal", jadwal.getIdJadwal());
        intent.putExtra("id_user", userId);
        intent.putExtra("jurusan", jadwal.getJurusan());
        intent.putExtra("tanggal_jam", jadwal.getTanggalJamBerangkat());
        intent.putExtra("harga", jadwal.getHarga());
        intent.putExtra("jumlah_kursi", 5);

        // Tambahkan log untuk memastikan semua data terkirim
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Log.d("JadwalListActivity", "Extra: " + key + " = " + extras.get(key));
            }
        }

        startActivity(intent);
    }
}