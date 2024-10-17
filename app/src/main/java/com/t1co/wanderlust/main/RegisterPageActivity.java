package com.t1co.wanderlust.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.t1co.wanderlust.R;

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

        // Initialize VolleyHandler
        volleyHandler = VolleyHandler.getInstance(this);

        // Initialize UI components
        EditText txtFullName = findViewById(R.id.txt_nama_user);
        EditText txtEmail = findViewById(R.id.txt_email);
        EditText txtUsername = findViewById(R.id.txt_username);
        txtPassword = findViewById(R.id.txt_password);
        txtConfirmPassword = findViewById(R.id.txt_konfirmasi_password);
        Button registerButton = findViewById(R.id.register_button);

        // Set hint on focus for all fields
        setHintOnFocus(txtFullName, "Full Name");
        setHintOnFocus(txtEmail, "Email");
        setHintOnFocus(txtUsername, "Username");
        setHintOnFocus(txtPassword, "Password");
        setHintOnFocus(txtConfirmPassword, "Konfirmasi Password");

        // Set up password visibility toggles
        setupPasswordVisibilityToggle(txtPassword, () -> isPasswordVisible);
        setupPasswordVisibilityToggle(txtConfirmPassword, () -> isConfirmPasswordVisible);

        // Register button logic
        registerButton.setOnClickListener(v -> {
            String fullName = txtFullName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String username = txtUsername.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String confirmPassword = txtConfirmPassword.getText().toString().trim();

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

    private interface PasswordVisibilityCheck {
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
            Toast.makeText(this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email tidak sesuai!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password dan Konfirmasi Password tidak sama!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void performRegistration(String fullName, String email, String username, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("nama_user", fullName);
        params.put("email", email);
        params.put("username", username);
        params.put("password", password);

        volleyHandler.makePostRequest(ApiConfig.REGISTER_URL, params, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                handleRegistrationResponse(result);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RegisterPageActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleRegistrationResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");

            switch (status) {
                case "data_tersimpan":
                    Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginPageActivity.class));
                    finish();
                    break;
                case "input_tidak_valid":
                    String message = jsonObject.getString("message");
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    break;
                case "username_sudah_ada":
                    Toast.makeText(this, "Username sudah terdaftar!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Registrasi gagal!", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Terjadi kesalahan dalam pemrosesan data!", Toast.LENGTH_SHORT).show();
        }
    }
}