package com.t1co.wanderlust;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterPageActivity extends AppCompatActivity {

    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);


        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);


        EditText txtUsername = findViewById(R.id.txt_username);
        txtPassword = findViewById(R.id.txt_password);
        txtConfirmPassword = findViewById(R.id.txt_konfirmasi_password);
        Button registerButton = findViewById(R.id.register_button);

        setHintOnFocus(txtUsername, "Username");
        setHintOnFocus(txtPassword, "Password");
        setHintOnFocus(txtConfirmPassword, "Konfirmasi Password");


        txtPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (txtPassword.getRight() - txtPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility(txtPassword);
                    return true;
                }
            }
            return false;
        });


        txtConfirmPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (txtConfirmPassword.getRight() - txtConfirmPassword.getCompoundDrawables()[2].getBounds().width())) {
                    toggleConfirmPasswordVisibility(txtConfirmPassword);
                    return true;
                }
            }
            return false;
        });

        registerButton.setOnClickListener(v -> {
            String username = txtUsername.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String confirmPassword = txtConfirmPassword.getText().toString().trim();


            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterPageActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterPageActivity.this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterPageActivity.this, "Password dan Konfirmasi Password tidak sama!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.apply();

            Toast.makeText(RegisterPageActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(RegisterPageActivity.this, LoginPageActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void togglePasswordVisibility(EditText passwordField) {
        if (isPasswordVisible) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.view, 0);
        }
        isPasswordVisible = !isPasswordVisible;  // Toggle status
        passwordField.setSelection(passwordField.getText().length());
    }


    private void toggleConfirmPasswordVisibility(EditText passwordField) {
        if (isConfirmPasswordVisible) {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
        } else {
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.view, 0);
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible;  // Toggle status
        passwordField.setSelection(passwordField.getText().length());  // Menjaga kursor di akhir
    }

    private void setHintOnFocus(EditText editText, String hint) {
        editText.setText(hint);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setText("");
            } else if (editText.getText().toString().isEmpty()) {
                editText.setText(hint);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    editText.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}

