package com.t1co.wanderlust.main.KritikDanSaran;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class KritikDanSaranPageActivity extends AppCompatActivity {

    private EditText editTextJudul;
    private EditText editTextPesan;
    private Button buttonKirim;
    private static final String TAG = "KritikDanSaranPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kritik_dan_saran_page);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        editTextJudul = findViewById(R.id.editTextJudul);
        editTextPesan = findViewById(R.id.editTextPesan);
        buttonKirim = findViewById(R.id.buttonKirim);
    }

    private void setupListeners() {
        buttonKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimPesan();
            }
        });
    }

    private void kirimPesan() {
        String judul = editTextJudul.getText().toString().trim();
        String pesan = editTextPesan.getText().toString().trim();

        if (TextUtils.isEmpty(judul)) {
            editTextJudul.setError("Judul saran tidak boleh kosong");
            return;
        }

        if (TextUtils.isEmpty(pesan)) {
            editTextPesan.setError("Pesan tidak boleh kosong");
            return;
        }

        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String token = preferences.getString("token", "");

        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, "Token tidak ditemukan, silakan login kembali.", Toast.LENGTH_SHORT).show();
            return;
        }

        kirimDataKeApi(judul, pesan, token);
    }

    private void kirimDataKeApi(String judul, String pesan, String token) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("judul_saran", judul);
            jsonBody.put("detail_saran", pesan);

            final String requestBody = jsonBody.toString();

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + token);
            headers.put("Content-Type", "application/json");

            Log.d(TAG, "Sending data: " + requestBody);

            VolleyHandler.getInstance(this).makeJsonPostRequest(
                    ApiConfig.KRITIK_URL,
                    headers,
                    requestBody,
                    new VolleyHandler.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d(TAG, "API Response: " + result);
                            try {
                                JSONObject response = new JSONObject(result);
                                String status = response.getString("status");
                                String message = response.getString("message");

                                if ("berhasil".equals(status)) {
                                    Toast.makeText(KritikDanSaranPageActivity.this,
                                            "Terima kasih atas saran Anda!",
                                            Toast.LENGTH_LONG).show();
                                    editTextJudul.setText("");
                                    editTextPesan.setText("");
                                } else {
                                    Toast.makeText(KritikDanSaranPageActivity.this,
                                            message,
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing response: " + e.getMessage());
                                Toast.makeText(KritikDanSaranPageActivity.this,
                                        "Terjadi kesalahan saat memproses response",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG, "API Error: " + error);
                            Toast.makeText(KritikDanSaranPageActivity.this,
                                    "Gagal mengirim saran: " + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error creating request: " + e.getMessage());
            Toast.makeText(this, "Terjadi kesalahan saat membuat request",
                    Toast.LENGTH_LONG).show();
        }
    }
}