package com.t1co.wanderlust;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText txtUsername = findViewById(R.id.txt_username);
        EditText txtPassword = findViewById(R.id.txt_password);

        setHintOnFocus(txtUsername, "Username");
        setHintOnFocus(txtPassword, "Password");

        // Mengatur listener untuk berpindah fokus saat tombol enter ditekan
        txtUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                txtPassword.requestFocus(); // Pindah fokus ke EditText password
                return true; // Menandakan bahwa event telah ditangani
            }
            return false;
        });

        // Inisialisasi TextView untuk "buat akun disini!"
        TextView buatAkunTextView = findViewById(R.id.buatakun1);

        // Listener untuk mengubah warna saat disentuh
        buatAkunTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                buatAkunTextView.setTextColor(Color.RED); // Ubah warna saat disentuh
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                buatAkunTextView.setTextColor(Color.BLUE); // Kembalikan ke warna normal
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Pindah ke RegisterPageActivity saat diklik
                    Intent intent = new Intent(LoginPageActivity.this, RegisterPageActivity.class); startActivity(intent);
                }
            }
            return true;
        });
    }

    private void setHintOnFocus(EditText editText, String hint) {
        editText.setText(hint);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setText(""); // Menghilangkan teks saat difokuskan
            } else if (editText.getText().toString().isEmpty()) {
                editText.setText(hint); // Mengembalikan hint jika tidak ada teks
            }
        });

        // Tambahkan TextWatcher untuk menghapus hint saat mengetik
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