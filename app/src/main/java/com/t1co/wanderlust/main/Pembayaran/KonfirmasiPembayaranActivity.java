package com.t1co.wanderlust.main.Pembayaran;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.Dashboard.DashboardNavigation1;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class KonfirmasiPembayaranActivity extends AppCompatActivity {
    private EditText etTanggalPembayaran, etJamPembayaran, etNoResi;
    private ImageView ivDatePicker, ivTimePicker;
    private TextView tvKeberangkatan, tvTujuan, tvTanggalBerangkat, tvHarga, tvStatusPembayaran, tvIdPesan, etIdPesan;
    private Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_pembayaran);

        initViews();

        Intent intent = getIntent();
        String idPesan = intent.getStringExtra("id_pesan");

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        ivDatePicker.setOnClickListener(v -> showDatePicker());
        ivTimePicker.setOnClickListener(v -> showTimePicker());
        btnSimpan.setOnClickListener(v -> konfirmasiPembayaran());

        if (idPesan != null && !idPesan.isEmpty()) {
            getDetailPembayaran(idPesan, token);
        } else {
            Toast.makeText(this, "ID Pemesanan tidak ditemukan.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        tvIdPesan = findViewById(R.id.tvIdPesan);
        etIdPesan = findViewById(R.id.etIdPesan);
        etTanggalPembayaran = findViewById(R.id.etTanggalPembayaran);
        etJamPembayaran = findViewById(R.id.etJamPembayaran);
        etNoResi = findViewById(R.id.No_Resi);
        ivDatePicker = findViewById(R.id.ivDatePicker);
        ivTimePicker = findViewById(R.id.ivTimePicker);
        tvKeberangkatan = findViewById(R.id.keberangkatan);
        tvTujuan = findViewById(R.id.tujuan);
        tvTanggalBerangkat = findViewById(R.id.tvTanggalBerangkat);
        tvHarga = findViewById(R.id.harga);
        tvStatusPembayaran = findViewById(R.id.tvStatusPembayaran);
        btnSimpan = findViewById(R.id.btnSimpan);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth1) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth1);
                    etTanggalPembayaran.setText(date);
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute1) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, minute1);
                    etJamPembayaran.setText(time);
                },
                hour, minute, true);
        timePickerDialog.show();
    }

    private void getDetailPembayaran(String idPesan, String token) {
        String url = ApiConfig.PEMBAYARAN_URL + "?id_pesan=" + idPesan;

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        VolleyHandler.getInstance(this).makeGetRequestWithHeaders(url, headers, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    if (jsonResponse.getString("status").equals("sukses")) {
                        JSONObject data = jsonResponse.getJSONObject("data");

                        if (data != null) {
                            tvTanggalBerangkat.setText(data.getString("tgl_berangkat"));
                            tvKeberangkatan.setText(data.getString("Keberangkatan"));
                            tvTujuan.setText(data.getString("Tujuan"));
                            tvHarga.setText("Rp. " + data.getString("harga"));
                            tvStatusPembayaran.setText(data.getString("status"));
                            tvIdPesan.setText(data.getString("id_pesan"));
                            etIdPesan.setText(data.getString(("id_pesan")));

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


    private void konfirmasiPembayaran() {
        String noResi = etNoResi.getText().toString().trim();
        String tglTransfer = etTanggalPembayaran.getText().toString().trim();
        String jamTransfer = etJamPembayaran.getText().toString().trim();
        String idPesan = getIntent().getStringExtra("id_pesan"); // Ambil ID Pesan dari Intent
        String status = "Dalam Proses"; // Status yang diinginkan

        if (noResi.isEmpty() || tglTransfer.isEmpty() || jamTransfer.isEmpty() || idPesan == null || idPesan.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject postData = new JSONObject();
        try {
            postData.put("no_resi", noResi);
            postData.put("tgl_transfer", tglTransfer);
            postData.put("jam_transfer", jamTransfer);
            postData.put("id_pesan", Integer.parseInt(idPesan));
            postData.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ApiConfig.KONFIRMASIPEMBAYARAN; // Ganti dengan URL yang sesuai
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("Content-Type", "application/json");

        VolleyHandler.getInstance(this).makeJsonPostRequest(url, headers, postData.toString(), new VolleyHandler.VolleyCallback() {

            @Override
            public void onSuccess(String result) {
                try {
                    // Parsing JSON dari respons server
                    JSONObject jsonResponse = new JSONObject(result);
                    String statusResponse = jsonResponse.optString("status", ""); // Ambil status, default kosong
                    String message = jsonResponse.optString("message", "Tidak ada pesan dari server."); // Pesan default jika tidak ada

                    // Validasi status
                    if ("sukses".equalsIgnoreCase(statusResponse)) { // Cocokkan dengan respons API
                        // Panggil HandleVerificationResponse jika status "sukses"
                        HandleVerificationResponse(result);
                    } else {
                        // Jika status bukan "sukses", tampilkan pesan error
                        showCustomErrorDialog("Konfirmasi Gagal", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Tangani kesalahan parsing JSON
                    showCustomErrorDialog("Kesalahan Parsing", "Terjadi kesalahan saat membaca data server.");
                }
            }



            @Override
            public void onError(String error) {
                Toast.makeText(KonfirmasiPembayaranActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void HandleVerificationResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.optString("status", "");

            if ("sukses".equalsIgnoreCase(status)) { // Sesuaikan dengan API
                String message = jsonObject.optString("message", "Konfirmasi berhasil.");
                SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Belum Bayar", false); // Pastikan nilai yang sesuai
                editor.apply();

                // Tampilkan dialog sukses
                showSuccessDialog("Konfirmasi Berhasil", message);
            } else {
                String error = jsonObject.optString("message", "Kesalahan tidak dikenal.");
                showCustomErrorDialog("Konfirmasi Gagal", error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showCustomErrorDialog("Kesalahan Parsing", "Respons server tidak sesuai format yang diharapkan.");
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

        iconView.setImageResource(R.drawable.ic_info); // Gambar untuk informasi
        titleView.setText(title);
        messageView.setText(message);

        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            // Arahkan ke KonfirmasiPembayaranActivity
            Intent intent = new Intent(KonfirmasiPembayaranActivity.this, DashboardNavigation1.class);
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

