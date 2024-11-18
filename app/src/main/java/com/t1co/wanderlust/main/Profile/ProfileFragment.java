package com.t1co.wanderlust.main.Profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private TextView nameEdit, fullnameEdit, emailEdit, passwordEdit;
    private SharedPreferences sharedPreferences;
    private VolleyHandler volleyHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("user_data", MODE_PRIVATE);
        volleyHandler = VolleyHandler.getInstance(requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_page, container, false);

        initializeViews(view);
        fetchUserData();

        return view;
    }

    private void initializeViews(View view) {
        nameEdit = view.findViewById(R.id.nameEdit);
        fullnameEdit = view.findViewById(R.id.fullnameEdit);
        emailEdit = view.findViewById(R.id.emailEdit);
        passwordEdit = view.findViewById(R.id.passwordEdit);
        Button editButton = view.findViewById(R.id.buttonedit1);

        editButton.setOnClickListener(this::OnKlickEditProfile);
    }

    private void fetchUserData() {
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            showToast("Token tidak ditemukan");
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        volleyHandler.makeGetRequestWithHeaders(ApiConfig.PROFILE_URL, headers, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");

                    if (status.equals("berhasil")) {
                        JSONObject userData = jsonResponse.getJSONObject("data");
                        updateUI(userData);
                    } else {
                        String message = jsonResponse.getString("message");
                        showToast(message);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    showToast("Terjadi kesalahan saat mempres data");
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
        if (getActivity() == null || !isAdded()) return;

        requireActivity().runOnUiThread(() -> {
            try {
                nameEdit.setText(String.format("Username: %s", userData.getString("username")));
                fullnameEdit.setText(String.format("Fullname: %s", userData.getString("nama_user")));
                emailEdit.setText(String.format("Email: %s", userData.getString("email")));
                passwordEdit.setText(String.format("Password: %s", userData.getString("password")));
            } catch (JSONException e) {
                Log.e(TAG, "Error updating UI: " + e.getMessage());
                showToast("Terjadi kesalahan saat menampilkan data");
            }
        });
    }

    private void showToast(String message) {
        if (getActivity() == null || !isAdded()) return;

        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        );
    }

    public void OnKlickEditProfile(View view) {
        Log.d(TAG, "ProfileEditKlick");

        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            showToast("Token tidak ditemukan, harap login ulang.");
            return;
        }

        Intent intent = new Intent(requireActivity(), ProfileEditActivity.class);
        intent.putExtra("token", token);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchUserData();
    }
}