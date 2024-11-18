package com.t1co.wanderlust.main.Berita;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeritaFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BeritaAdapter adapter;
    private List<BeritaModel> beritaList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_berita_page, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_berita);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        beritaList = new ArrayList<>();
        adapter = new BeritaAdapter(getContext(), beritaList);
        recyclerView.setAdapter(adapter);

        loadBerita();

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
        return view;
    }

    private void loadBerita() {
        if (getContext() == null) return;
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        VolleyHandler.getInstance(getContext()).makeGetRequestWithHeaders(
                ApiConfig.TAMPILBERITA_URL,
                headers,
                new VolleyHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("status");

                            if (status.equals("berhasil")) {
                                beritaList.clear();
                                JSONArray dataArray = jsonObject.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject beritaObject = dataArray.getJSONObject(i);

                                    BeritaModel berita = new BeritaModel(
                                            beritaObject.getString("judul_berita"),
                                            beritaObject.getString("foto_berita"),
                                            beritaObject.getString("konten_berita"),
                                            beritaObject.getString("tgl_berita")
                                    );

                                    beritaList.add(berita);
                                }

                                adapter.notifyDataSetChanged();
                            } else {
                                showToast("Tidak ada berita tersedia");
                            }

                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("Terjadi kesalahan saat memproses data");
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        showToast(error);
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
        );
    }

    private void refreshData() {
        loadBerita();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
