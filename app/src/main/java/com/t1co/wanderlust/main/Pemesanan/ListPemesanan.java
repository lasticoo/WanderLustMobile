package com.t1co.wanderlust.main.Pemesanan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.PemilihanPaket.PemilihanPaket1Activity;
import com.t1co.wanderlust.main.PemilihanPaket.PemilihanPaket2Activity;
import com.t1co.wanderlust.main.PemilihanPaket.PemilihanPaket3Activity;

public class ListPemesanan extends AppCompatActivity {

    private static final String TAG = "ListPemesanan";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pemesanan);

        // Menambahkan padding untuk system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi tombol
        ImageButton btnPaket1 = findViewById(R.id.btnPaket1);
        ImageButton btnPaket2 = findViewById(R.id.btnPaket2);
        ImageButton btnPaket3 = findViewById(R.id.btnPaket3);

        // Cek jika tombol tidak ditemukan
        if (btnPaket1 == null || btnPaket2 == null || btnPaket3 == null) {
            Log.e(TAG, "Salah satu tombol tidak ditemukan. Periksa ID di layout XML.");
            return; // Hentikan jika ada tombol yang null
        }

        // Listener untuk tombol paket 1
        btnPaket1.setOnClickListener(v -> {
            Log.d(TAG, "Paket 1 button clicked");
            Toast.makeText(this, "Tombol Paket 1 Diklik", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PemilihanPaket1Activity.class));
        });

        // Listener untuk tombol paket 2
        btnPaket2.setOnClickListener(v -> {
            Log.d(TAG, "Paket 2 button clicked");
            Toast.makeText(this, "Tombol Paket 2 Diklik", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PemilihanPaket2Activity.class));
        });

        // Listener untuk tombol paket 3
        btnPaket3.setOnClickListener(v -> {
            Log.d(TAG, "Paket 3 button clicked");
            Toast.makeText(this, "Tombol Paket 3 Diklik", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PemilihanPaket3Activity.class));
        });
    }
}
