package com.t1co.wanderlust.main.Berita;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.t1co.wanderlust.R;

public class ContohBerita extends AppCompatActivity {
    private ImageView ivFotoBerita;
    private TextView tvJudulBerita;
    private TextView tvTanggalBerita;
    private TextView tvKontenBerita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contoh_berita);

        ivFotoBerita = findViewById(R.id.iv_foto_berita);
        tvJudulBerita = findViewById(R.id.tv_judul_berita);
        tvTanggalBerita = findViewById(R.id.tv_tanggal_berita);
        tvKontenBerita = findViewById(R.id.tv_konten_berita);

        Intent intent = getIntent();
        if (intent != null) {
            String judul = intent.getStringExtra("judul");
            String tanggal = intent.getStringExtra("tanggal");
            String konten = intent.getStringExtra("konten");
            String foto = intent.getStringExtra("foto");

            tvJudulBerita.setText(judul);
            tvTanggalBerita.setText(tanggal);

            if (konten != null) {
                tvKontenBerita.setText(HtmlCompat.fromHtml(konten, HtmlCompat.FROM_HTML_MODE_LEGACY));
                tvKontenBerita.setMovementMethod(LinkMovementMethod.getInstance()); // Untuk tautan
            }

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(1024, 1024)
                    .centerCrop();

            Glide.with(this)
                    .load(foto)
                    .apply(options)
                    .error(R.drawable.ic_error)
                    .into(ivFotoBerita);
        }

        Button buttonBack = findViewById(R.id.buttonback);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
