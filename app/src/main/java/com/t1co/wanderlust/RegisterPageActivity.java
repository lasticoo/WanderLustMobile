package com.t1co.wanderlust;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log; // Import log untuk debugging
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

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

        // Toggle password visibility
        txtPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (txtPassword.getRight() - txtPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility(txtPassword);
                    return true;
                }
            }
            return false;
        });

        // Toggle confirm password visibility
        txtConfirmPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (txtConfirmPassword.getRight() - txtConfirmPassword.getCompoundDrawables()[2].getBounds().width())) {
                    toggleConfirmPasswordVisibility(txtConfirmPassword);
                    return true;
                }
            }
            return false;
        });

        // Register button logic
        registerButton.setOnClickListener(v -> {
            String fullName = txtFullName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String username = txtUsername.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String confirmPassword = txtConfirmPassword.getText().toString().trim();

            // Check for empty fields
            if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterPageActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for valid email format
            if (!isValidEmail(email)) {
                Toast.makeText(RegisterPageActivity.this, "Email tidak sesuai!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check for password length
            if (password.length() < 6) {
                Toast.makeText(RegisterPageActivity.this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if password and confirm password match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterPageActivity.this, "Password dan Konfirmasi Password tidak sama!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform registration without sending the confirm password
            new RegisterUserTask(fullName, email, username, password).execute();
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
        isPasswordVisible = !isPasswordVisible;
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
        isConfirmPasswordVisible = !isConfirmPasswordVisible;
        passwordField.setSelection(passwordField.getText().length());
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
    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|yahoo\\.co\\.id)$";
        return email.matches(emailPattern);
    }
     private class RegisterUserTask extends AsyncTask<Void, Void, String> {
        private String fullName;
        private String email;
        private String username;
        private String password;

        RegisterUserTask(String fullName, String email, String username, String password) {
            this.fullName = fullName;
            this.email = email;
            this.username = username;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            try {
                URL url = new URL("http://192.168.1.6/apiwanderlustmobile/login_register/buatakun.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "nama_user=" + fullName + "&email=" + email + "&username=" + username + "&password=" + password;
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(postData);
                wr.flush();
                wr.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseBuilder.append(inputLine);
                }
                in.close();

                response = responseBuilder.toString();
                Log.d("RegisterResponse", "Response: " + response); // Log respons mentah
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            handleResponse(result);
        }

        private void handleResponse(String response) {
            // Parse the JSON response
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");

                if (status.equals("data_tersimpan")) {
                    Toast.makeText(RegisterPageActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                    // Redirect to login page
                    Intent intent = new Intent(RegisterPageActivity.this, LoginPageActivity.class);
                    startActivity(intent);
                    finish();
                } else if (status.equals("input_tidak_valid")) {
                    String message = jsonObject.getString("message");
                    Toast.makeText(RegisterPageActivity.this, message, Toast.LENGTH_SHORT).show();
                } else if (status.equals("username_sudah_ada")) {
                    Toast.makeText(RegisterPageActivity.this, "Username sudah terdaftar!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterPageActivity.this, "Registrasi gagal!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(RegisterPageActivity.this, "Terjadi kesalahan dalam pemrosesan data!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
