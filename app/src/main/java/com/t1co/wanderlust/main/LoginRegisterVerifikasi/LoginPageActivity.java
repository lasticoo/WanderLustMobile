package com.t1co.wanderlust.main.LoginRegisterVerifikasi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.Dashboard.DashboardNavigation;
import com.t1co.wanderlust.main.ForgotPassword.ForgotPasswordActivity;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginPageActivity extends AppCompatActivity {

    private EditText txtPassword;
    private EditText txtemail;
    private boolean isPasswordVisible = false;
    private SharedPreferences sharedPreferences;
    private VolleyHandler volleyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        volleyHandler = VolleyHandler.getInstance(this);

        txtemail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_password);
        Button loginButton = findViewById(R.id.login_button);
        Button registerButton = findViewById(R.id.belumpunyakun1);
        Button forgotpasswordbutton = findViewById(R.id.forgotpassword);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtemail.setOnClickListener(v -> txtemail.setSelection(txtemail.getText().length()));
        txtPassword.setOnClickListener(v -> txtPassword.setSelection(txtPassword.getText().length()));

        setHintOnFocus(txtemail, "Email");
        setHintOnFocus(txtPassword, "Password");

        txtemail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                txtPassword.requestFocus();
                return true;
            }
            return false;
        });

        txtPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (txtPassword.getRight() - txtPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        loginButton.setOnClickListener(v -> performLogin());

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPageActivity.this, RegisterPageActivity.class);
            startActivity(intent);
        });

        forgotpasswordbutton.setOnClickListener(v ->{
            Intent intent = new Intent(LoginPageActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String email = txtemail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginPageActivity.this, "Email dan Password harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiConfig.LOGIN_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if (status.equals("berhasil")) {
                            Toast.makeText(LoginPageActivity.this, message, Toast.LENGTH_SHORT).show();

                            String id_user = jsonResponse.getString("id_user");
                            String nama_user = jsonResponse.getString("nama_user");
                            String emailUser = jsonResponse.getString("email");
                            String username = jsonResponse.getString("username");
                            String passwordUser = jsonResponse.getString("password");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id_user", id_user);
                            editor.putString("nama_user", nama_user);
                            editor.putString("email", emailUser);
                            editor.putString("username", username);
                            editor.putString("password", passwordUser);
                            String token = jsonResponse.getString("token");
                            editor.putString("token", token);
                            editor.apply();
                            Log.d("Login", "User data saved: " + id_user + ", " + nama_user);

                            Intent intent = new Intent(LoginPageActivity.this, DashboardNavigation.class);
                            intent.putExtra("email", emailUser);
                            intent.putExtra("id_user", id_user);
                            startActivity(intent);
                            finish();
                        } else if (status.equals("akun_belum_terverifikasi")) {
                            Toast.makeText(LoginPageActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginPageActivity.this, VerifikasiAkun.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginPageActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginPageActivity.this, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(LoginPageActivity.this, "Kesalahan jaringan: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        volleyHandler.addToRequestQueue(stringRequest);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            txtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
        } else {
            txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            txtPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.view, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        txtPassword.setSelection(txtPassword.getText().length());
    }

    private void setHintOnFocus(EditText editText, String hint) {
        editText.setHint(hint);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (editText.getText().toString().equals(hint)) {
                    editText.setText("");
                }
            } else if (editText.getText().toString().isEmpty()) {
                editText.setText("");
                editText.setHint(hint);
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
