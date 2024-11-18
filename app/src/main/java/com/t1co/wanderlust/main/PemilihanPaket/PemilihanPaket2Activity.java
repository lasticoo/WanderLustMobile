package com.t1co.wanderlust.main.PemilihanPaket;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.t1co.wanderlust.R;

public class PemilihanPaket2Activity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private Spinner paketSpinner;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemilihan_paket2);

        // Inisialisasi views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        paketSpinner = findViewById(R.id.paketSpinner);
        submitButton = findViewById(R.id.submitButton);

        // Setup spinner
        String[] paketList = {"Paket Basic", "Paket Premium", "Paket VIP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, paketList);
        paketSpinner.setAdapter(adapter);

        // Setup button click listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String selectedPaket = paketSpinner.getSelectedItem().toString();

                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(PemilihanPaket2Activity.this,
                            "Mohon isi semua field", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tampilkan pesan sukses
                String message = "Nama: " + name + "\nEmail: " + email +
                        "\nPaket: " + selectedPaket;
                Toast.makeText(PemilihanPaket2Activity.this,
                        "Data berhasil disimpan\n" + message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}