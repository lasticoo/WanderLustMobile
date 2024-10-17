package com.t1co.wanderlust.main;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class VolleyHandler {
    private static VolleyHandler instance;
    private RequestQueue requestQueue;
    private static Context context;

    private static final int TIMEOUT_MS = 30000;
    private static final String TAG = "VolleyHandler";

    private VolleyHandler(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyHandler getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyHandler(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    // Method untuk POST request
    public void makePostRequest(String url, final Map<String, String> params,
                                final VolleyCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: " + response);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = handleError(error);
                        Log.e(TAG, "Error: " + error.toString());
                        callback.onError(errorMessage);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        // Mengatur timeout dan retry policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        addToRequestQueue(stringRequest);
    }

    // Method untuk GET request
    public void makeGetRequest(String url, final VolleyCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response: " + response);
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = handleError(error);
                        Log.e(TAG, "Error: " + error.toString());
                        callback.onError(errorMessage);
                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        addToRequestQueue(stringRequest);
    }

    // Method untuk menangani error
    private String handleError(VolleyError error) {
        String errorMessage = "Terjadi kesalahan jaringan";

        if (error instanceof com.android.volley.NoConnectionError) {
            errorMessage = "Tidak ada koneksi internet. Silakan periksa koneksi Anda!";
        } else if (error instanceof com.android.volley.NetworkError) {
            errorMessage = "Tidak dapat terhubung ke server. Periksa koneksi internet Anda!";
        } else if (error instanceof com.android.volley.ServerError) {
            errorMessage = "Server sedang bermasalah. Silakan coba lagi nanti!";
        } else if (error instanceof com.android.volley.TimeoutError) {
            errorMessage = "Koneksi timeout. Silakan coba lagi!";
        } else if (error instanceof com.android.volley.AuthFailureError) {
            errorMessage = "Gagal autentikasi. Silakan login kembali!";
        }

        return errorMessage;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
