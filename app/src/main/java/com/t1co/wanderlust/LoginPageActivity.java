package com.t1co.wanderlust;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPageActivity extends AppCompatActivity {

    private EditText txtPassword;
    private boolean isPasswordVisible = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);


        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText txtUsername = findViewById(R.id.txt_username);
        txtPassword = findViewById(R.id.txt_password);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.belumpunyakun1);

        setHintOnFocus(txtUsername, "Username");
        setHintOnFocus(txtPassword, "Password");


        txtUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                txtPassword.requestFocus();
                return true;
            }
            return false;
        });

        // Listener untuk ikon show/hide password
        txtPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (txtPassword.getRight() - txtPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            String inputUsername = txtUsername.getText().toString().trim();
            String inputPassword = txtPassword.getText().toString().trim();

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(LoginPageActivity.this, "Username dan Password harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            String registeredUsername = sharedPreferences.getString("username", "");
            String registeredPassword = sharedPreferences.getString("password", "");

            if (inputUsername.equals(registeredUsername) && inputPassword.equals(registeredPassword)) {

                Toast.makeText(LoginPageActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginPageActivity.this, DashboardNavigation.class);
                intent.putExtra("username", inputUsername);  // Kirim username
                startActivity(intent);
                finish();
            } else {

                Toast.makeText(LoginPageActivity.this, "Username atau Password salah!", Toast.LENGTH_SHORT).show();
            }
        });


        registerButton.setOnClickListener(v -> {

            Intent intent = new Intent(LoginPageActivity.this, RegisterPageActivity.class);
            startActivity(intent);
        });
    }

    // Fungsi untuk toggle show/hide password
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            txtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0); // Ikon sembunyikan
        } else {
            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            txtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.view, 0); // Ikon tampilkan
        }
        isPasswordVisible = !isPasswordVisible; // Toggle status
        txtPassword.setSelection(txtPassword.getText().length()); // Menjaga kursor di akhir
    }

    // Fungsi untuk menampilkan/mengembalikan hint pada EditText saat fokus
    private void setHintOnFocus(EditText editText, String hint) {
        editText.setText(hint);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setText(""); // Menghilangkan teks saat difokuskan
            } else if (editText.getText().toString().isEmpty()) {
                editText.setText(hint); // Mengembalikan hint jika tidak ada teks
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    editText.setHint(""); // Menghilangkan hint saat ada teks
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
