package com.t1co.wanderlust.main.Galeri;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;
import com.t1co.wanderlust.main.koneksi.ApiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GaleriPageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GaleriAdapter adapter;
    private List<GaleriItem> galeriItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        galeriItemList = new ArrayList<>();
        adapter = new GaleriAdapter(this, galeriItemList);
        recyclerView.setAdapter(adapter);

        fetchHistoryData();
    }

    private void fetchHistoryData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        VolleyHandler.getInstance(this).makeGetRequestWithHeaders(ApiConfig.HISTORY, headers, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject response = new JSONObject(result);
                    if (response.getString("status").equals("berhasil")) {
                        JSONArray dataArray = response.getJSONArray("data");
                        galeriItemList.clear();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObject = dataArray.getJSONObject(i);
                            String imageUrl = dataObject.getString("nama_foto");
                            String keterangan = dataObject.getString("keterangan_foto");
                            galeriItemList.add(new GaleriItem(imageUrl, keterangan));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(GaleriPageActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("HistoryPage", "JSON Parsing Error: " + e.getMessage());
                    Toast.makeText(GaleriPageActivity.this, "Data parsing error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("HistoryPage", "Error: " + error);
                Toast.makeText(GaleriPageActivity.this, "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}