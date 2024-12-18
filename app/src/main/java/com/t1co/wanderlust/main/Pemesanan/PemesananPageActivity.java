package com.t1co.wanderlust.main.Pemesanan;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.Dashboard.DashboardNavigation1;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PemesananPageActivity extends AppCompatActivity {
    private TextView etIdJadwal, etEmail, etIdUser ;
    private Spinner spNoKursi;
    private TextView tvKursiStatus;
    private Button btnPesan;
    private VolleyHandler volleyHandler;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pemesasan_page);

        // Ambil data dari intent
        String idJadwal = getIntent().getStringExtra("id_jadwal");
        String idUser  = getIntent().getStringExtra("id_user");

        initializeViews();

        // Set nilai id_jadwal dan id_user dari intent
        if (idJadwal != null) {
            etIdJadwal.setText(idJadwal);
        }
        if (idUser  != null) {
            etIdUser .setText(idUser );
            getEmailByIdUser (idUser );
        }

        loadKursiStatus(idJadwal);

        btnPesan.setOnClickListener(view -> submitPemesanan());
    }

    private void initializeViews() {
        etIdJadwal = findViewById(R.id.idjadwal);
        etEmail = findViewById(R.id.emailEditText);
        etIdUser  = findViewById(R.id.iduser);
        spNoKursi = findViewById(R.id.no_kursi);
        tvKursiStatus = findViewById(R.id.heckKursiStatus);
        btnPesan = findViewById(R.id.btn_pesan);

        volleyHandler = new VolleyHandler(this);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void getEmailByIdUser (String idUser ) {
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        Map<String, String> params = new HashMap<>();
        params.put("action", "getEmailByIdUser ");
        params.put("id_user", idUser );

        volleyHandler.makePostRequestWithHeaders(
                ApiConfig.PROFILE_URL,
                headers,
                params,
                new VolleyHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            if (response.getString("status").equals("berhasil")) {
                                JSONObject data = response.getJSONObject("data");
                                etEmail.setText(data.getString("email"));
                            } else {
                                Toast.makeText(PemesananPageActivity.this,
                                        response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PemesananPageActivity.this,
                                    "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(PemesananPageActivity.this,
                                "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadKursiStatus(String idJadwal) {
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT). show();
            return;
        }

        // Membuat JSONObject untuk dikirim
        JSONObject params = new JSONObject();
        try {
            params.put("id_jadwal", idJadwal);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating JSON object", Toast.LENGTH_SHORT).show();
            return; // Keluar jika terjadi kesalahan saat membuat JSON
        }

        progressDialog.setMessage("Memuat status kursi...");
        progressDialog.show();

        // Menggunakan VolleyHandler untuk mengirim permintaan JSON
        VolleyHandler.getInstance(this).makeJsonPostRequest(
                ApiConfig.PEMESANAN_URL,
                new HashMap<String, String>() {{
                    put("Authorization", "Bearer " + token);
                    put("Content-Type", "application/json"); // Menetapkan jenis konten
                }},
                params.toString(), // Mengirimkan JSON sebagai string
                new VolleyHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        progressDialog.dismiss();
                        try {
                            JSONObject response = new JSONObject(result);
                            if (response.getString("status").equals("sukses")) {
                                JSONObject data = response.getJSONObject("data");
                                JSONObject kursiStatus = data.getJSONObject("kursi_status");

                                ArrayList<String> availableSeats = new ArrayList<>();
                                StringBuilder kursiInfo = new StringBuilder();
                                Iterator<String> keys = kursiStatus.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    String status = kursiStatus.getString(key);

                                    Log.d("KursiItem", "Kursi " + key + ": " + status);

                                    if ("Kosong".equals(status)) {
                                        availableSeats.add(key); // Menambahkan kursi yang kosong
                                    }
                                    kursiInfo.append(key).append(" : ").append(status).append("\n");
                                }

                                // Update UI
                                setupSpinner(availableSeats);
                                tvKursiStatus.setText(kursiInfo.toString()); // Update TextView dengan status kursi
                            } else {
                                Toast.makeText(PemesananPageActivity.this,
                                        response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PemesananPageActivity.this,
                                    "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        progressDialog.dismiss();
                        Toast.makeText(PemesananPageActivity.this,
                                "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSpinner(ArrayList<String> availableSeats) {
        if (availableSeats == null || availableSeats.isEmpty()) {
            availableSeats = new ArrayList<>();
            availableSeats.add("Tidak ada kursi tersedia");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                availableSeats
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNoKursi.setAdapter(adapter);
    }

    private void submitPemesanan() {
        String idJadwal = etIdJadwal.getText().toString();
        String idUser = etIdUser.getText().toString();
        String noKursi = spNoKursi.getSelectedItem().toString();
        String status = "Belum Bayar";

        if (idJadwal.isEmpty() || idUser.isEmpty() || noKursi.equals("Tidak ada kursi tersedia")) {
            Toast.makeText(this, "ID Jadwal, ID User, dan No Kursi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("id_jadwal", Integer.parseInt(idJadwal));
            params.put("id_user", Integer.parseInt(idUser));
            params.put("no_kursi", noKursi);
            params.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating JSON object", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("Content-Type", "application/json");

        progressDialog.setMessage("Mengirim pemesanan...");
        progressDialog.show();

        volleyHandler.makeJsonPostRequest(
                ApiConfig.PEMESANAN_URL,
                headers,
                params.toString(),
                new VolleyHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        progressDialog.dismiss();
                        HandleVerificationResponse(result);
                    }

                    @Override
                    public void onError(String error) {
                        progressDialog.dismiss();
                        try {
                            JSONObject errorResponse = new JSONObject(error);
                            String status = errorResponse.optString("status", "");
                            String message = errorResponse.optString("message", "");

                            if (status.equals("gagal") && message.equals("Kursi sudah dipesan")) {
                                showCustomErrorDialog("Pemesanan Gagal", message);
                            } else {
                                showCustomErrorDialog("Pemesanan Gagal",
                                        errorResponse.optString("message", "Terjadi kesalahan, coba lagi nanti."));
                            }
                        } catch (JSONException e) {
                            showCustomErrorDialog("Pemesanan Gagal", "Terjadi kesalahan, coba lagi nanti.");
                        }
                    }
                }
        );
    }

    private void HandleVerificationResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            String message = jsonObject.getString("message");

            if (status.equals("sukses") && message.equals("Pemesanan berhasil dilakukan")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Belum Bayar", true);
                editor.apply();

                showSuccessDialog("Pemesanan Berhasil", message);
            } else if (status.equals("gagal") && message.equals("Kursi sudah dipesan")) {
                showCustomErrorDialog("Pemesanan Gagal", message);
            } else {
                showCustomErrorDialog("Pemesanan Gagal", message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showCustomErrorDialog("ERROR!", "Terjadi Kesalahan Data!");
        }
    }

    private void showSuccessDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        ImageView iconView = dialog.findViewById(R.id.dialogIcon);
        TextView titleView = dialog.findViewById(R.id.dialogTitle);
        TextView messageView = dialog.findViewById(R.id.dialogMessage);
        Button okButton = dialog.findViewById(R.id.dialogButton);

        iconView.setImageResource(R.drawable.ic_info);
        titleView.setText(title);
        messageView.setText(message);

        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(PemesananPageActivity.this, DashboardNavigation1.class);
            startActivity(intent);
            finish();
        });

        dialog.show();
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
        okButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private enum DialogType {
        ERROR,
        WARNING,
        INFO
    }

    }
