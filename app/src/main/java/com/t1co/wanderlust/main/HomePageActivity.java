package com.t1co.wanderlust.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.t1co.wanderlust.R;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page); // Pastikan ini sesuai dengan nama layout Anda

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke LoginPageActivity saat tombol ditekan
                Intent intent = new Intent(HomePageActivity.this, LoginPageActivity.class);
                startActivity(intent);
            }
        });
    }
}