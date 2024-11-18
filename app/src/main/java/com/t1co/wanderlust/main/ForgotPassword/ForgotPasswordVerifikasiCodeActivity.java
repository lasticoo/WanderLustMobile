package com.t1co.wanderlust.main.ForgotPassword;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONObject;

public class ForgotPasswordVerifikasiCodeActivity extends AppCompatActivity {
    private String email;
    private EditText[] otpEditTexts;
    private Button verifyButton;
    private Dialog loadingDialog; // Dialog loading

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_verifikasi_code);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Mengambil email dari Intent
        email = getIntent().getStringExtra("email");

        if (email == null || email.isEmpty()) {
            Log.e("EmailError", "Email tidak ditemukan!");
            Toast.makeText(this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }

        otpEditTexts = new EditText[] {
                findViewById(R.id.otp1),
                findViewById(R.id.otp2),
                findViewById(R.id.otp3),
                findViewById(R.id.otp4),
                findViewById(R.id.otp5),
                findViewById(R.id.otp6)
        };

        verifyButton = findViewById(R.id.verify_button);

        setupOtpEditTexts();

        verifyButton.setOnClickListener(v -> {
            StringBuilder otp = new StringBuilder();
            for (EditText editText : otpEditTexts) {
                otp.append(editText.getText().toString().trim());
            }

            if (otp.length() == otpEditTexts.length) {
                verifyOtp(otp.toString());
            } else {
                Toast.makeText(ForgotPasswordVerifikasiCodeActivity.this, "Kode OTP harus diisi lengkap!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOtpEditTexts() {
        for (int i = 0; i < otpEditTexts.length; i++) {
            final int index = i;
            otpEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0 && count == 1 && index > 0) {
                        otpEditTexts[index - 1].requestFocus();
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpEditTexts.length - 1) {
                        otpEditTexts[index + 1].requestFocus();
                    }
                }
            });
        }
    }

    private void verifyOtp(String otp) {
        // Tampilkan dialog loading sebelum permintaan API
        showLoadingDialog();

        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("verifikasi_code", otp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("VolleyRequest", "Email: " + email + ", OTP: " + otp);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ApiConfig.verifikasi_Otp, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissLoadingDialog();

                        try {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            // Menangani respons dari API
                            if (status.equals("success")) {
                                showSuccessDialog("Verifikasi Berhasil", "Kode OTP telah diverifikasi.");
                            } else {
                                showErrorDialog("Verifikasi Gagal", message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        // Sembunyikan dialog loading saat terjadi error
                        dismissLoadingDialog();

                        Log.e("VolleyError", "Error: " + error.getMessage());
                        showErrorDialog("Verifikasi Gagal", "Terjadi kesalahan saat mengirim permintaan.");
                    }
                });

        VolleyHandler.getInstance(this).addToRequestQueue(request);
    }

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.custome_loading_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(loadingDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        loadingDialog.getWindow().setAttributes(layoutParams);

        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    // Menampilkan dialog sukses
    private void showSuccessDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_verifotp_berhasil);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        ImageView iconView = dialog.findViewById(R.id.dialogIcon);
        TextView titleView = dialog.findViewById(R.id.dialogTitle);
        TextView messageView = dialog.findViewById(R.id.dialogMessage);
        Button okButton = dialog.findViewById(R.id.dialogButton);

        iconView.setImageResource(R.drawable.ic_info);
        titleView.setText(title);
        messageView.setText(message);

        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(ForgotPasswordVerifikasiCodeActivity.this, ForgotPasswordResetPassword.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    // Menampilkan dialog error
    private void showErrorDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        ImageView iconView = dialog.findViewById(R.id.dialogIcon);
        TextView titleView = dialog.findViewById(R.id.dialogTitle);
        TextView messageView = dialog.findViewById(R.id.dialogMessage);
        Button retryButton = dialog.findViewById(R.id.dialogButton);

        iconView.setImageResource(R.drawable.ic_error);
        titleView.setText(title);
        messageView.setText(message);

        retryButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
