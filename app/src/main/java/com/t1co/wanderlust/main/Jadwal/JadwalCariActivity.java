package com.t1co.wanderlust.main.Jadwal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JadwalCariActivity extends AppCompatActivity {
    private Spinner spinnerKeberangkatan;
    private Spinner spinnerDestinasi;
    private DatePicker datePicker;
    private Button btnPesanSekarang;
    private SharedPreferences sharedPreferences;
    private VolleyHandler volleyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cari_jadwal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d("CariJadwal", "Initializing views...");
        initializeViews();

        Log.d("CariJadwal", "Fetching spinner data...");
        fetchSpinnerData();

        Log.d("CariJadwal", "Setting up date picker...");
        setupDatePicker();

        setupButton();
    }

    private void initializeViews() {
        spinnerKeberangkatan = findViewById(R.id.spinnerKeberangkatan);
        spinnerDestinasi = findViewById(R.id.spinnerDestinasi);
        datePicker = findViewById(R.id.datePicker);
        btnPesanSekarang = findViewById(R.id.pesansekarang);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        volleyHandler = VolleyHandler.getInstance(this);
    }

    private void fetchSpinnerData() {
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        volleyHandler.makeGetRequestWithHeaders(ApiConfig.SPINNER_URL, headers, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    if (response.getString("status").equals("sukses")) {
                        JSONArray keberangkatanArray = response.getJSONArray("keberangkatan");
                        JSONArray tujuanArray = response.getJSONArray("tujuan");

                        ArrayList<String> keberangkatanList = new ArrayList<>();
                        ArrayList<String> tujuanList = new ArrayList<>();

                        for (int i = 0; i < keberangkatanArray.length(); i++) {
                            keberangkatanList.add(keberangkatanArray.getJSONObject(i).getString("alamat"));
                        }

                        for (int i = 0; i < tujuanArray.length(); i++) {
                            tujuanList.add(tujuanArray.getJSONObject(i).getString("nama_destinasi"));
                        }

                        ArrayAdapter<String> keberangkatanAdapter = new ArrayAdapter<>(JadwalCariActivity.this, android.R.layout.simple_spinner_item, keberangkatanList);
                        keberangkatanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerKeberangkatan.setAdapter(keberangkatanAdapter);

                        ArrayAdapter<String> tujuanAdapter = new ArrayAdapter<>(JadwalCariActivity.this, android.R.layout.simple_spinner_item, tujuanList);
                        tujuanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDestinasi.setAdapter(tujuanAdapter);
                    } else {
                        Toast.makeText(JadwalCariActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(JadwalCariActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(JadwalCariActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePicker() {
        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                (view, year, monthOfYear, dayOfMonth) -> {
                });
    }

    private void setupButton() {
        btnPesanSekarang.setOnClickListener(v -> {
            String fromLocation = spinnerKeberangkatan.getSelectedItem().toString();
            String toLocation = spinnerDestinasi.getSelectedItem().toString();

            String travelDate = String.format("%d-%02d-%02d",
                    datePicker.getYear(),
                    (datePicker.getMonth() + 1),
                    datePicker.getDayOfMonth());

            Intent intent = new Intent(JadwalCariActivity.this, JadwalListActivity.class);
            intent.putExtra("from_location", fromLocation);
            intent.putExtra("to_location", toLocation);
            intent.putExtra("travel_date", travelDate);

            startActivity(intent);
        });
    }
}