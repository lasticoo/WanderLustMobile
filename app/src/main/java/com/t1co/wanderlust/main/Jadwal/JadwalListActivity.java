package com.t1co.wanderlust.main.Jadwal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.Pemesanan.PemesasanPageActivity;

import com.t1co.wanderlust.main.Jadwal.JadwalAdapter;

import java.util.ArrayList;
import java.util.List;

public class JadwalListActivity extends AppCompatActivity {
    private RecyclerView rvJadwal;
    private JadwalAdapter adapter;
    private List<Jadwal> jadwalList;
    private TextView tvRute, tvTanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_list);

        // Initialize views
        initViews();

        // Get intent data
        handleIntentData();

        // Setup recycler view with optimization
        setupRecyclerViewOptimized();

        // Load data in background
        loadDataInBackground();
    }

    private void initViews() {
        rvJadwal = findViewById(R.id.rvJadwal);
        tvRute = findViewById(R.id.tvRute);
        tvTanggal = findViewById(R.id.tvTanggal);
    }

    private void handleIntentData() {
        String fromLocation = getIntent().getStringExtra("from_location");
        String toLocation = getIntent().getStringExtra("to_location");
        String travelDate = getIntent().getStringExtra("travel_date");

        // Format rute text
        String ruteText = String.format("%s → %s", fromLocation, toLocation);
        tvRute.setText(ruteText);
        tvTanggal.setText(travelDate);
    }

    private void setupRecyclerViewOptimized() {
        // Initialize list
        jadwalList = new ArrayList<>();

        // Setup layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setInitialPrefetchItemCount(4); // Prefetch optimization

        // Setup RecyclerView
        rvJadwal.setLayoutManager(layoutManager);
        rvJadwal.setHasFixedSize(true); // Optimization for fixed size
        rvJadwal.setItemViewCacheSize(20); // Increase view cache

        // Initialize adapter
        adapter = new JadwalAdapter(this, jadwalList, jadwal -> {
            // Create booking intent
            navigateToBooking(jadwal);
        });

        // Set adapter
        rvJadwal.setAdapter(adapter);
    }

    private void loadDataInBackground() {
        // Run on background thread
        new Thread(() -> {
            // Create dummy data
            List<Jadwal> dummyData = createDummyData();

            // Update UI on main thread
            runOnUiThread(() -> {
                jadwalList.clear();
                jadwalList.addAll(dummyData);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private List<Jadwal> createDummyData() {
        List<Jadwal> dummyList = new ArrayList<>();

        dummyList.add(new Jadwal(
                1,
                "Bus Maju Jaya",
                "Executive",
                150000,
                "08:00",
                "14:00",
                20,
                getIntent().getStringExtra("from_location"),
                getIntent().getStringExtra("to_location"),
                getIntent().getStringExtra("travel_date")
        ));

        dummyList.add(new Jadwal(
                2,
                "Bus Sejahtera",
                "Business",
                120000,
                "09:30",
                "15:30",
                15,
                getIntent().getStringExtra("from_location"),
                getIntent().getStringExtra("to_location"),
                getIntent().getStringExtra("travel_date")
        ));

        dummyList.add(new Jadwal(
                3,
                "Bus Mulia Trans",
                "Executive",
                180000,
                "10:00",
                "16:00",
                25,
                getIntent().getStringExtra("from_location"),
                getIntent().getStringExtra("to_location"),
                getIntent().getStringExtra("travel_date")
        ));

        return dummyList;
    }

    private void navigateToBooking(Jadwal jadwal) {
        Intent intent = new Intent(JadwalListActivity.this, PemesasanPageActivity.class);
        intent.putExtra("jadwal_id", jadwal.getId());
        intent.putExtra("bus_name", jadwal.getBusName());
        intent.putExtra("bus_type", jadwal.getBusType());
        intent.putExtra("price", jadwal.getPrice());
        intent.putExtra("departure_time", jadwal.getDepartureTime());
        intent.putExtra("arrival_time", jadwal.getArrivalTime());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear references
        rvJadwal.setAdapter(null);
        jadwalList.clear();
    }
}