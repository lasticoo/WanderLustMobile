package com.t1co.wanderlust.main.Jadwal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.Jadwal.JadwalListActivity;

public class JadwalCariActivity extends AppCompatActivity {
    private Spinner spinnerKeberangkatan;
    private Spinner spinnerDestinasi;
    private DatePicker datePicker;
    private Button btnPesanSekarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
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

            Log.d("CariJadwal", "Setting up spinners...");
            setupSpinners();

            Log.d("CariJadwal", "Setting up date picker...");
            setupDatePicker();

            // Setup button click listener
            setupButton();

        } catch (Exception e) {
            Log.e("CariJadwal", "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        spinnerKeberangkatan = findViewById(R.id.spinnerKeberangkatan);
        spinnerDestinasi = findViewById(R.id.spinnerDestinasi);
        datePicker = findViewById(R.id.datePicker);
        btnPesanSekarang = findViewById(R.id.pesansekarang); // Tambahkan ini
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> keberangkatanAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.lokasi_keberangkatan,
                android.R.layout.simple_spinner_item
        );
        keberangkatanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKeberangkatan.setAdapter(keberangkatanAdapter);

        ArrayAdapter<CharSequence> destinasiAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.destinasi_wisata,
                android.R.layout.simple_spinner_item
        );
        destinasiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestinasi.setAdapter(destinasiAdapter);
    }

    private void setupDatePicker() {
        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Handle perubahan tanggal jika diperlukan
                    }
                }
        );
    }

    private void setupButton() {
        btnPesanSekarang.setOnClickListener(v -> {
            // Ambil data dari form
            String fromLocation = spinnerKeberangkatan.getSelectedItem().toString();
            String toLocation = spinnerDestinasi.getSelectedItem().toString();

            // Format tanggal
            String travelDate = String.format("%d-%02d-%02d",
                    datePicker.getYear(),
                    (datePicker.getMonth() + 1),
                    datePicker.getDayOfMonth());

            // Buat intent ke ListJadwalActivity
            Intent intent = new Intent(JadwalCariActivity.this, JadwalListActivity.class);

            // Kirim data melalui intent
            intent.putExtra("from_location", fromLocation);
            intent.putExtra("to_location", toLocation);
            intent.putExtra("travel_date", travelDate);

            // Jalankan activity baru
            startActivity(intent);
        });
    }
}