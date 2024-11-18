package com.t1co.wanderlust.main.Profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {
    private static final String TAG = "ProfileEditActivity";
    private EditText editUsername, editNamaUser;
    private TextView editEmail, passwordEditText;
    private Button btnUpdate;
    private SharedPreferences sharedPreferences;
    private VolleyHandler volleyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        volleyHandler = VolleyHandler.getInstance(this);

        initializeViews();
        fetchUserProfile();
    }

    private void initializeViews() {
        editUsername = findViewById(R.id.nameEditText);
        editNamaUser = findViewById(R.id.fullnameEditText);
        editEmail = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        btnUpdate = findViewById(R.id.btn_simpan);

        btnUpdate.setOnClickListener(v -> updateUserProfile());
    }

    private void fetchUserProfile() {
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            showToast("Token tidak ditemukan");
            finish();
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        volleyHandler.makeGetRequestWithHeaders(ApiConfig.EDITPROFILE_URL, headers, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");

                    if (status.equals("success")) {
                        JSONObject userData = jsonResponse.getJSONObject("data");
                        updateUI(userData);
                    } else {
                        String message = jsonResponse.getString("message");
                        showToast(message);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    showToast("Terjadi kesalahan saat memproses data");
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Network Error: " + error);
                showToast("Gagal terhubung ke server");
            }
        });
    }

    private void updateUI(JSONObject userData) {
        runOnUiThread(() -> {
            try {
                editUsername.setText(userData.getString("username"));
                editNamaUser.setText(userData.getString("nama_user"));
                editEmail.setText(userData.getString("email"));
                passwordEditText.setText(userData.getString("password"));
            } catch (JSONException e) {
                Log.e(TAG, "Error updating UI: " + e.getMessage());
                showToast("Terjadi kesalahan saat menampilkan data");
            }
        });
    }

    private void updateUserProfile() {
        String username = editUsername.getText().toString().trim();
        String namaUser = editNamaUser.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || namaUser.isEmpty() || email.isEmpty()) {
            showToast("Semua field harus diisi");
            return;
        }

        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            showToast("Token tidak ditemukan");
            finish();
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        Map<String, String> params = new HashMap<>();

        if (!username.isEmpty()) {
            params.put("username", username);
        }
        if (!namaUser.isEmpty()) {
            params.put("nama_user", namaUser);
        }

        if (params.isEmpty()) {
            showToast("Tidak ada data yang diubah");
            return;
        }

        volleyHandler.makePostRequestWithHeaders(ApiConfig.EDITPROFILE_URL, headers, params, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    showToast(message);
                    if (status.equals("success")) {
                        finish();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    showToast("Terjadi kesalahan saat memproses data");
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Network Error: " + error);
                showToast("Gagal terhubung ke server");
            }
        });
    }


    private void showToast(final String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }
}