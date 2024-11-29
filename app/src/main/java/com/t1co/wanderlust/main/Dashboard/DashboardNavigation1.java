package com.t1co.wanderlust.main.Dashboard;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.Berita.BeritaFragment;
import com.t1co.wanderlust.main.Profile.ProfileFragment;
import com.t1co.wanderlust.main.LoginRegisterVerifikasi.VerifikasiAkun;
import com.t1co.wanderlust.main.StatusPembayaran.StatusPembayaranFragment;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardNavigation1 extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigation;
    private DashboardFragment dashboardFragment;
    private ProfileFragment profileFragment;
    private StatusPembayaranFragment statuspembayaranfragment;
    private BeritaFragment beritaFragment;
    private SharedPreferences sharedPreferences;
    private VolleyHandler volleyHandler;
    private String email;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_dashboard);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        email = sharedPreferences.getString("email", null);

        bottomNavigation = findViewById(R.id.bottomview);
        bottomNavigation.setOnItemSelectedListener(this);

        dashboardFragment = new DashboardFragment();
        profileFragment = new ProfileFragment();
        beritaFragment = new BeritaFragment();
        statuspembayaranfragment = new StatusPembayaranFragment(); // Inisialisasi fragment

        loadFragment(statuspembayaranfragment);

        volleyHandler = VolleyHandler.getInstance(this);

        fetchUserDataFromDashboard();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                loadFragment(dashboardFragment);
                return true;

            case R.id.berita:
                loadFragment(beritaFragment);
                return true;

            case R.id.profile:
                loadFragment(profileFragment);
                return true;
            case R.id.statuspembayaran:
                loadFragment(statuspembayaranfragment);
                return true;

            default:
                return false;
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragment, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void fetchUserDataFromDashboard() {

        String token = sharedPreferences.getString("token", "");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        volleyHandler.makeGetRequestWithHeaders(ApiConfig.DASHBOARD_URL, headers, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("API_RESPONSE", result);

                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");

                    if (status.equals("berhasil")) {
                        JSONObject userData = jsonResponse.getJSONObject("data");
                        String id_user = userData.getString("id_user");
                        String username = userData.getString("username");
                        String email = userData.getString("email");
                        String nama_user = userData.getString("nama_user");
                        String password_user = userData.getString("password");


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("id_user", id_user);
                        editor.putString("username", username);
                        editor.putString("email", email);
                        editor.putString("nama_user", nama_user);
                        editor.putString("password", password_user);
                        editor.apply();
                    } else {
                        Toast.makeText(DashboardNavigation1.this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DashboardNavigation1.this, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("API_ERROR", error);
                Toast.makeText(DashboardNavigation1.this, "Kesalahan jaringan: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showVerificationRequiredDialog() {
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
        titleView.setText("Akses Ditolak");
        messageView.setText("Anda belum verifikasi akun, silakan verifikasi akun Anda dulu.");

        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(DashboardNavigation1.this, VerifikasiAkun.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
}
