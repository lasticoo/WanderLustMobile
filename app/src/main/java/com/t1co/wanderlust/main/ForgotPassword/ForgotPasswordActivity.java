package com.t1co.wanderlust.main.ForgotPassword;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button nextButton;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.txt_email_fotgot);
        nextButton = findViewById(R.id.login_button);

        // Initialize the loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.custome_loading_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (!email.isEmpty()) {
                    sendForgotPasswordRequest(email);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Email harus diisi!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendForgotPasswordRequest(String email) {
        loadingDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        VolleyHandler.getInstance(this).makePostRequest(ApiConfig.forgot_password, params, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();

                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    if (status.equals("success")) {
                        showSuccessDialog("Berhasil", message);
                    } else if (status.equals("input tidak valid") || status.equals("gagal")) {
                        showErrorDialog("Gagal", message);
                    } else {
                        showErrorDialog("Error", "Terjadi kesalahan yang tidak terduga.");
                    }
                } catch (JSONException e) {
                    showErrorDialog("Error", "Format respons tidak valid.");
                }
            }

            @Override
            public void onError(String error) {
                loadingDialog.dismiss();
                showErrorDialog("Gagal", "Gagal mengirim permintaan: " + error);
            }
        });
    }

    private void showSuccessDialog(String title, String message) {
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
        Button okButton = dialog.findViewById(R.id.dialogButton);

        iconView.setImageResource(R.drawable.ic_info);
        titleView.setText(title);
        messageView.setText(message);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ForgotPasswordActivity.this, ForgotPasswordVerifikasiCodeActivity.class);
                intent.putExtra("email", emailEditText.getText().toString().trim());
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }

    private void showErrorDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

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

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
