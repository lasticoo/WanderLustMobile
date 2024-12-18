package com.t1co.wanderlust.main.StatusPembayaran;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.CetakTiket.CetakTiketActivity;
import com.t1co.wanderlust.main.LoginRegisterVerifikasi.LoginPageActivity;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusPembayaranAdapter extends RecyclerView.Adapter<StatusPembayaranAdapter.ViewHolder> {
    private static final String TAG = "StatusPembayaranAdapter";
    private final List<StatusPembayaranModel> statusList;
    private final Context context;
    private OnItemClickListener onItemClickListener;
    private final SharedPreferences sharedPreferences;
    private RefreshCallback refreshCallback;

    public interface RefreshCallback {
        void onDataRefreshNeeded();
    }

    public void setRefreshCallback(RefreshCallback callback) {
        this.refreshCallback = callback;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public StatusPembayaranAdapter(List<StatusPembayaranModel> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("user_data", MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status_pembayaran, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatusPembayaranModel item = statusList.get(position);

        holder.tvIdPesan.setText(item.getIdPesan() != null ? item.getIdPesan() : "ID tidak ditemukan");
        holder.tvTujuan.setText(item.getTujuan());
        holder.tvWaktu.setText(item.getWaktu());
        holder.tvHarga.setText(item.getHarga());
        holder.tvStatus.setText(item.getStatus());

        if ("Lunas".equals(item.getStatus())) {
            holder.btnBatal.setText("Cetak Tiket");
            holder.btnBatal.setBackgroundColor(ContextCompat.getColor(context, R.color.hijau));
            holder.btnBatal.setOnClickListener(v -> {
                if (validateToken()) {
                    Intent intent = new Intent(context, CetakTiketActivity.class);
                    intent.putExtra("id_pesan", item.getIdPesan());
                    intent.putExtra("rute", item.getTujuan());
                    intent.putExtra("waktu_berangkat", item.getWaktu());
                    intent.putExtra("harga", item.getHarga());
                    intent.putExtra("status", item.getStatus());
                    context.startActivity(intent);
                }
            });
        } else {
            holder.btnBatal.setText("Batal");
            holder.btnBatal.setBackgroundColor(ContextCompat.getColor(context, R.color.merah));
            holder.btnBatal.setOnClickListener(v -> {
                if (validateToken()) {
                    showCancelDialog(item.getIdPesan());
                }
            });
        }
    }

    private boolean validateToken() {
        String token = getStoredToken();
        if (token.isEmpty()) {
            handleTokenError();
            return false;
        }
        Log.d(TAG, "Token validated: " + token);
        return true;
    }

    private String getStoredToken() {
        String token = sharedPreferences.getString("token", "");
        Log.d(TAG, "Retrieved token: " + (token.isEmpty() ? "empty" : "exists"));
        return token;
    }

    private void handleTokenError() {
        Log.e(TAG, "Token error occurred");
        Toast.makeText(context, "Sesi anda telah berakhir. Silahkan login kembali", Toast.LENGTH_LONG).show();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent loginIntent = new Intent(context, LoginPageActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(loginIntent);
    }

    private void showCancelDialog(String idPesan) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_cancel, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(true);

        AlertDialog dialog = builder.create();

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        AppCompatButton btnCancelYes = dialogView.findViewById(R.id.btnCancelYes);
        AppCompatButton btnCancelNo = dialogView.findViewById(R.id.btnCancelNo);

        dialogTitle.setText("Konfirmasi");
        dialogMessage.setText("Apakah Anda ingin membatalkan pemesanan ID: " + idPesan + "?");

        btnCancelYes.setOnClickListener(v -> {
            dialog.dismiss();
            if (validateToken()) {
                cancelBooking(idPesan);
            }
        });

        btnCancelNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void cancelBooking(String idPesan) {
        String token = sharedPreferences.getString("token", "");
        if (token.isEmpty()) {
            Toast.makeText(context, "Token tidak ditemukan", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("id_pesan", idPesan);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating JSON object", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("Content-Type", "application/json");

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Memproses pembatalan...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        VolleyHandler.getInstance(context).makeJsonPostRequest(
                ApiConfig.BATALPEMESANAN,
                headers,
                params.toString(),
                new VolleyHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        progressDialog.dismiss();
                        try {
                            JSONObject response = new JSONObject(result);
                            if (response.getString("status").equals("berhasil")) {
                                Toast.makeText(context, "Pemesanan berhasil dibatalkan", Toast.LENGTH_SHORT).show();
                                showCustomErrorDialog("Pembatalan Berhasil", "Pemesanan ID: " + idPesan + " berhasil dibatalkan");
                            } else {
                                showCustomErrorDialog("Pembatalan Gagal",
                                        response.optString("message", "Gagal membatalkan pemesanan"));
                            }
                        } catch (JSONException e) {
                            showCustomErrorDialog("Error", "Terjadi kesalahan, coba lagi nanti.");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        progressDialog.dismiss();
                        try {
                            JSONObject errorResponse = new JSONObject(error);
                            String message = errorResponse.optString("message", "Terjadi kesalahan, coba lagi nanti.");
                            showCustomErrorDialog("Pembatalan Gagal", message);
                        } catch (JSONException e) {
                            showCustomErrorDialog("Error", "Terjadi kesalahan, coba lagi nanti.");
                        }
                    }
                }
        );
    }

    private void showCustomErrorDialog(String title, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
        dialog.getWindow().setAttributes(layoutParams);

        ImageView iconView = dialog.findViewById(R.id.dialogIcon);
        TextView titleView = dialog.findViewById(R.id.dialogTitle);
        TextView messageView = dialog.findViewById(R.id.dialogMessage);
        Button okButton = dialog.findViewById(R.id.dialogButton);

        iconView.setImageResource(R.drawable.ic_error);
        titleView.setText(title);
        messageView.setText(message);

        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            // Refresh data after dialog closes
            if (refreshCallback != null) {
                refreshCallback.onDataRefreshNeeded();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdPesan, tvTujuan, tvWaktu, tvHarga, tvStatus;
        Button btnBatal;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tvIdPesan = itemView.findViewById(R.id.tvIdPesan);
            tvTujuan = itemView.findViewById(R.id.tvTujuan);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnBatal = itemView.findViewById(R.id.btnBatal);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
