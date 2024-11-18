package com.t1co.wanderlust.main.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.t1co.wanderlust.R;

public class ProfilePageActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilePageActivity.this, ProfileEditActivity.class);
                startActivity(intent);
            }
        });
    }

    public void OnKlickEditProfile(View view) {
        Log.d(TAG, "ProfileEditKlick");
        Intent intent = new Intent(this, ProfileEditActivity.class); // Menggunakan this untuk Activity
        startActivity(intent);
    }
}
