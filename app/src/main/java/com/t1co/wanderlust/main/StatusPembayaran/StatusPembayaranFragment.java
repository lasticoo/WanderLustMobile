package com.t1co.wanderlust.main.StatusPembayaran;

import static com.t1co.wanderlust.main.koneksi.ApiConfig.CekStatusPembayaran_URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.Pembayaran.PembayaranPageActivity;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusPembayaranFragment extends Fragment {

    private RecyclerView recyclerViewStatus;
    private SwipeRefreshLayout swipeRefreshLayout;
    private StatusPembayaranAdapter adapter;
    private List<StatusPembayaranModel> statusList;
    private SharedPreferences sharedPreferences;
    private VolleyHandler volleyHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_status_pembayaran_page, container, false);

        // Initialize views
        recyclerViewStatus = view.findViewById(R.id.recyclerViewStatus);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Set up RecyclerView with LinearLayoutManager
        recyclerViewStatus.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize status list and adapter
        statusList = new ArrayList<>();
        adapter = new StatusPembayaranAdapter(statusList, getContext());
        recyclerViewStatus.setAdapter(adapter);

        // Initialize SharedPreferences and VolleyHandler
        sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        volleyHandler = VolleyHandler.getInstance(getContext());

        // Set up swipe-to-refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadDataFromServer);

        // Load initial data from server
        loadDataFromServer();

        // Tambahkan klik listener untuk item pada RecyclerView
        adapter.setOnItemClickListener((position) -> {
            StatusPembayaranModel selectedStatus = statusList.get(position);
            if ("Belum Bayar".equals(selectedStatus.getStatus())) {
                Intent intent = new Intent(getContext(), PembayaranPageActivity.class);
                intent.putExtra("id_pesan", selectedStatus.getIdPesan());
                startActivity(intent);
            }
        });

        return view;
    }

    private void initializeViews() {
        // View initialization from XML
        recyclerViewStatus = getView().findViewById(R.id.recyclerViewStatus);
        swipeRefreshLayout = getView().findViewById(R.id.swipeRefreshLayout);

        // Initialize VolleyHandler and SharedPreferences
        volleyHandler = VolleyHandler.getInstance(getContext());
        sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);

        // Initialize progress dialog (if needed later)
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }

    private void loadDataFromServer() {
        // Get the token from SharedPreferences
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(getContext(), "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        // Set up headers with the token for the request
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("Content-Type", "application/json");

        // Make a GET request using VolleyHandler
        volleyHandler.makeGetRequestWithHeaders(CekStatusPembayaran_URL, headers, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    // Parse the response from the server
                    JSONObject response = new JSONObject(result);
                    if (response.getString("status").equals("sukses")) {
                        JSONArray data = response.getJSONArray("data");
                        statusList.clear();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            statusList.add(new StatusPembayaranModel(
                                    item.getString("id_pesan"),
                                    item.getString("rute"),
                                    item.getString("waktu_berangkat"),
                                    item.getString("harga"),
                                    item.getString("status")
                            ));
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                adapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            });
                        }
                    } else {
                        showError("Gagal memuat data: " + response.getString("message"));
                    }
                } catch (JSONException e) {
                    showError("Error parsing data: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                showError(error);
            }
        });
    }

    private void showError(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            });
        }
    }
}
