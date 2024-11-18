package com.t1co.wanderlust.main.ForgotPassword;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
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

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordResetPassword extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private Button resetPasswordButton;
    private String email;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private Dialog loadingDialog;  // Mendeklarasikan loadingDialog di sini

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_reset_password);

        newPasswordEditText = findViewById(R.id.txt_password);
        confirmPasswordEditText = findViewById(R.id.txt_konfirmasi_password);
        resetPasswordButton = findViewById(R.id.verify_button);

        setPasswordVisibilityToggle(newPasswordEditText);
        setPasswordVisibilityToggle(confirmPasswordEditText);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        if (email == null) {
            Toast.makeText(this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        resetPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Kata sandi tidak cocok!", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(newPassword);
            }
        });
    }

    private void setPasswordVisibilityToggle(EditText passwordField) {
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordField.getRight() - passwordField.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility(passwordField);
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility(EditText passwordField) {
        if (passwordField == newPasswordEditText) {
            isPasswordVisible = !isPasswordVisible;
        } else if (passwordField == confirmPasswordEditText) {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
        }

        if (passwordField == newPasswordEditText) {
            if (isPasswordVisible) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.view, 0); // Icon mata terbuka
            } else {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0); // Icon mata tertutup
            }
        } else if (passwordField == confirmPasswordEditText) {
            if (isConfirmPasswordVisible) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.view, 0); // Icon mata terbuka
            } else {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0); // Icon mata tertutup
            }
        }

        passwordField.setSelection(passwordField.getText().length());
    }

    private void resetPassword(String password) {
        if (email == null) {
            showErrorDialog("Error", "Email tidak ditemukan.");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("password", password);
        params.put("email", email);

        showLoadingDialog();

        VolleyHandler.getInstance(this).makePostRequest(ApiConfig.verifikasi_Otp, params, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissLoadingDialog();
                showSuccessDialog("Reset Password Berhasil", "Kata sandi Anda berhasil diubah.");
            }

            @Override
            public void onError(String error) {
                dismissLoadingDialog();
                showErrorDialog("Reset Password Gagal", "Terjadi kesalahan saat mereset kata sandi. Coba lagi.");
            }
        });
    }

    // Menampilkan dialog loading
    private void showLoadingDialog() {
        loadingDialog = new Dialog(this);  // Membuat instance loadingDialog
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

    // Menyembunyikan dialog loading
    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
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

        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

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
