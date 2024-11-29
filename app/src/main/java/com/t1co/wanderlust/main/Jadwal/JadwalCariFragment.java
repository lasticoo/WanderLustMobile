package com.t1co.wanderlust.main.Jadwal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JadwalCariFragment extends Fragment {

    private Spinner spinnerKeberangkatan;
    private Spinner spinnerDestinasi;
    private DatePicker datePicker;
    private Button btnPesanSekarang;
    private SharedPreferences sharedPreferences;
    private VolleyHandler volleyHandler;

    public JadwalCariFragment() {
        // Diperlukan constructor kosong untuk fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cari_jadwal, container, false);

        Log.d("CariJadwalFragment", "Initializing views...");
        initializeViews(view);

        Log.d("CariJadwalFragment", "Fetching spinner data...");
        fetchSpinnerData();

        Log.d("CariJadwalFragment", "Setting up date picker...");
        setupDatePicker();

        setupButton();

        return view;
    }

    private void initializeViews(View view) {
        spinnerKeberangkatan = view.findViewById(R.id.spinnerKeberangkatan);
        spinnerDestinasi = view.findViewById(R.id.spinnerDestinasi);
        datePicker = view.findViewById(R.id.datePicker);
        btnPesanSekarang = view.findViewById(R.id.pesansekarang);
        sharedPreferences = requireActivity().getSharedPreferences("user_data", requireContext().MODE_PRIVATE);
        volleyHandler = VolleyHandler.getInstance(requireContext());
    }

    private void fetchSpinnerData() {
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(getContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        volleyHandler.makeGetRequestWithHeaders(ApiConfig.SPINNER_URL, headers, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    if (response.getString("status").equals("sukses")) {
                        JSONArray keberangkatanArray = response.getJSONArray("keberangkatan");
                        JSONArray tujuanArray = response.getJSONArray("tujuan");

                        ArrayList<String> keberangkatanList = new ArrayList<>();
                        ArrayList<String> tujuanList = new ArrayList<>();

                        for (int i = 0; i < keberangkatanArray.length(); i++) {
                            keberangkatanList.add(keberangkatanArray.getJSONObject(i).getString("alamat"));
                        }

                        for (int i = 0; i < tujuanArray.length(); i++) {
                            tujuanList.add(tujuanArray.getJSONObject(i).getString("nama_destinasi"));
                        }

                        ArrayAdapter<String> keberangkatanAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, keberangkatanList);
                        keberangkatanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerKeberangkatan.setAdapter(keberangkatanAdapter);

                        ArrayAdapter<String> tujuanAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tujuanList);
                        tujuanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDestinasi.setAdapter(tujuanAdapter);
                    } else {
                        Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePicker() {
        datePicker.init(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                (view, year, monthOfYear, dayOfMonth) -> {
                });
    }

    private void setupButton() {
        btnPesanSekarang.setOnClickListener(v -> {
            String fromLocation = spinnerKeberangkatan.getSelectedItem().toString();
            String toLocation = spinnerDestinasi.getSelectedItem().toString();

            String travelDate = String.format("%d-%02d-%02d",
                    datePicker.getYear(),
                    (datePicker.getMonth() + 1),
                    datePicker.getDayOfMonth());

            Intent intent = new Intent(getContext(), JadwalListActivity.class);
            intent.putExtra("from_location", fromLocation);
            intent.putExtra("to_location", toLocation);
            intent.putExtra("travel_date", travelDate);

            startActivity(intent);
        });
    }
}
