package com.t1co.wanderlust.main.LoginRegisterVerifikasi;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterPageActivity extends AppCompatActivity {

    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private SharedPreferences sharedPreferences;
    private VolleyHandler volleyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        volleyHandler = VolleyHandler.getInstance(this);

        EditText txtFullName = findViewById(R.id.txt_nama_user);
        EditText txtEmail = findViewById(R.id.txt_email);
        EditText txtUsername = findViewById(R.id.txt_username);
        txtPassword = findViewById(R.id.txt_password);
        txtConfirmPassword = findViewById(R.id.txt_konfirmasi_password);
        Button registerButton = findViewById(R.id.register_button);

        setHintOnFocus(txtFullName, "Full Name");
        setHintOnFocus(txtEmail, "Email");
        setHintOnFocus(txtUsername, "Username");
        setHintOnFocus(txtPassword, "Password");
        setHintOnFocus(txtConfirmPassword, "Konfirmasi Password");

        setupPasswordVisibilityToggle(txtPassword, () -> isPasswordVisible);
        setupPasswordVisibilityToggle(txtConfirmPassword, () -> isConfirmPasswordVisible);

        registerButton.setOnClickListener(v -> {
            String fullName = txtFullName.getText().toString().trim(); // Trim input
            String email = txtEmail.getText().toString().trim(); // Trim input
            String username = txtUsername.getText().toString().trim(); // Trim input
            String password = txtPassword.getText().toString().trim(); // Trim input
            String confirmPassword = txtConfirmPassword.getText().toString().trim(); // Trim input

            if (validateInputs(fullName, email, username, password, confirmPassword)) {
                performRegistration(fullName, email, username, password);
            }
        });
    }

    private void setupPasswordVisibilityToggle(EditText passwordField, PasswordVisibilityCheck visibilityCheck) {
        passwordField.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordField.getRight() - passwordField.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility(passwordField, visibilityCheck);
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility(EditText passwordField, PasswordVisibilityCheck visibilityCheck) {
        boolean isVisible = visibilityCheck.isVisible();
        if (isVisible) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.view, 0);
        }
        if (passwordField == txtPassword) {
            isPasswordVisible = !isPasswordVisible;
        } else if (passwordField == txtConfirmPassword) {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
        }
        passwordField.setSelection(passwordField.getText().length());
    }

    interface PasswordVisibilityCheck {
        boolean isVisible();
    }

    private void setHintOnFocus(EditText editText, String hint) {
        editText.setHint(hint);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setHint("");
            } else {
                if (editText.getText().toString().isEmpty()) {
                    editText.setHint(hint);
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|yahoo\\.co\\.id)$";
        return email.matches(emailPattern);
    }

    private boolean validateInputs(String fullName, String email, String username, String password, String confirmPassword) {
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showCustomErrorDialog("Input Tidak Valid", "Semua kolom harus diisi!", DialogType.ERROR);
            return false;
        }

        if (!isValidEmail(email)) {
            showCustomErrorDialog("Email Tidak Valid", "Format email tidak sesuai!", DialogType.WARNING);
            return false;
        }

        if (password.length() < 6) {
            showCustomErrorDialog("Password Tidak Valid", "Password minimal 6 karakter!", DialogType.WARNING);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showCustomErrorDialog("Password Tidak Cocok", "Password dan Konfirmasi Password tidak sama!", DialogType.ERROR);
            return false;
        }

        return true;
    }

    private void performRegistration(String fullName, String email, String username, String password) {
        final Dialog loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.custome_loading_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        TextView loadingMessage = loadingDialog.findViewById(R.id.loadingText);
        loadingMessage.setText("Memproses registrasi...");

        Map<String, String> params = new HashMap<>();
        params.put("nama_user", fullName);
        params.put("email", email);
        params.put("username", username);
        params.put("password", password);

        volleyHandler.makePostRequest(ApiConfig.REGISTER_URL, params, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();
                handleRegistrationResponse(result, email);
            }

            @Override
            public void onError(String error) {
                loadingDialog.dismiss();
                showCustomErrorDialog("Registrasi Gagal", error, DialogType.ERROR);
            }
        });
    }

    private void handleRegistrationResponse(String response, String email) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");

            if ("success".equals(status)) {
                String message = jsonObject.getString("message");
                showCustomErrorDialog("Registrasi Berhasil", message, DialogType.INFO);
                // Simpan email ke SharedPreferences
                sharedPreferences.edit().putString("email", email).apply();

                new android.os.Handler().postDelayed(
                        () -> {
                            startActivity(new Intent(this, VerifikasiAkun.class).putExtra("email", email)); // Pass email to the next activity
                            finish();
                        },
                        5000
                );
            } else if ("gagal".equals(status)) {
                String error = jsonObject.getString("error");
                showCustomErrorDialog("Registrasi Gagal", error, DialogType.ERROR);
            } else {
                String invalidMessage = jsonObject.getString("message");
                showCustomErrorDialog("Input Tidak Valid", invalidMessage, DialogType.WARNING);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showCustomErrorDialog("Error", "Terjadi kesalahan dalam pemrosesan data!", DialogType.ERROR);
        }
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showCustomErrorDialog(String title, String message, DialogType type) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        dialog.getWindow().setAttributes(layoutParams);

        ImageView iconView = dialog.findViewById(R.id.dialogIcon);
        TextView titleView = dialog.findViewById(R.id.dialogTitle);
        TextView messageView = dialog.findViewById(R.id.dialogMessage);
        Button okButton = dialog.findViewById(R.id.dialogButton);

        titleView.setText(title);
        messageView.setText(message);

        switch (type) {
            case ERROR:
                iconView.setImageResource(R.drawable.ic_error);
                break;
            case INFO:
                iconView.setImageResource(R.drawable.ic_info);
                break;
            case WARNING:
                iconView.setImageResource(R.drawable.ic_warning);
                break;
        }

        okButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    enum DialogType {
        ERROR, INFO, WARNING
    }
}
