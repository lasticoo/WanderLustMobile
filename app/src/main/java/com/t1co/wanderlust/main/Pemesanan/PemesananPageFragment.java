package com.t1co.wanderlust.main.Pemesanan;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PemesananPageFragment extends Fragment {
    private EditText etIdJadwal, etEmail, etIdUser;
    private Spinner spNoKursi;
    private TextView tvKursiStatus;
    private Button btnPesan;
    private VolleyHandler volleyHandler;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pemesasan_page, container, false);

        initializeViews(view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            String idJadwal = arguments.getString("id_jadwal");
            String idUser = arguments.getString("id_user");

            if (idJadwal != null) {
                etIdJadwal.setText(idJadwal);
            }
            if (idUser != null) {
                etIdUser.setText(idUser);
                getEmailByIdUser(idUser);
            }

            loadKursiStatus(idJadwal);
        }

        btnPesan.setOnClickListener(view1 -> submitPemesanan());

        return view;
    }

    private void initializeViews(View view) {
        etIdJadwal = view.findViewById(R.id.idjadwal);
        etEmail = view.findViewById(R.id.emailEditText);
        etIdUser = view.findViewById(R.id.iduser);
        spNoKursi = view.findViewById(R.id.no_kursi);
        tvKursiStatus = view.findViewById(R.id.heckKursiStatus);
        btnPesan = view.findViewById(R.id.btn_pesan);

        volleyHandler = new VolleyHandler(requireContext());
        sharedPreferences = requireContext().getSharedPreferences("user_data", getContext().MODE_PRIVATE);
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void getEmailByIdUser(String idUser) {
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(getContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        Map<String, String> params = new HashMap<>();
        params.put("action", "getEmailByIdUser");
        params.put("id_user", idUser);

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
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void loadKursiStatus(String idJadwal) {
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(getContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("id_jadwal", idJadwal);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error creating JSON object", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Memuat status kursi...");
        progressDialog.show();

        VolleyHandler.getInstance(requireContext()).makeJsonPostRequest(
                ApiConfig.PEMESANAN_URL,
                new HashMap<String, String>() {{
                    put("Authorization", "Bearer " + token);
                    put("Content-Type", "application/json");
                }},
                params.toString(),
                new VolleyHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        progressDialog.dismiss();
                        // Handle response logic
                    }

                    @Override
                    public void onError(String error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitPemesanan() {
        // Submit pemesanan logic
    }
}
