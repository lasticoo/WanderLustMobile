package com.t1co.wanderlust.main.CetakTiket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.t1co.wanderlust.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class CetakTiketActivity extends AppCompatActivity {

    private TextView idPesanView, destinasiView, tglBerangkatView, hargaView, statusView, namaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cetak_tiket);

        namaView = findViewById(R.id.nama_user);
        idPesanView = findViewById(R.id.id_pesan);
        destinasiView = findViewById(R.id.destinasi);
        tglBerangkatView = findViewById(R.id.tgl_berangkat);
        hargaView = findViewById(R.id.harga);
        statusView = findViewById(R.id.status);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String namaUser  = sharedPreferences.getString("nama_user", "Nama tidak ditemukan");

        Intent intent = getIntent();
        String idPesan = intent.getStringExtra("id_pesan");
        String rute = intent.getStringExtra("rute");
        String waktuBerangkat = intent.getStringExtra("waktu_berangkat");
        String harga = intent.getStringExtra("harga");
        String status = intent.getStringExtra("status");

        namaView.setText("Nama: " + namaUser );
        idPesanView.setText("ID Pesan: " + idPesan);
        destinasiView.setText("Rute: " + rute);
        tglBerangkatView.setText("Waktu Keberangkatan: " + waktuBerangkat);
        hargaView.setText("Harga: Rp " + harga);
        statusView.setText("Status: " + status);

        findViewById(R.id.btn_cetak_tiket).setOnClickListener(v -> {
            new GenerateTicketTask(namaUser , idPesan, rute, waktuBerangkat, harga, status).execute();
        });
    }

    private class GenerateTicketTask extends AsyncTask<Void, Void, File> {

        private String namaUser , idPesan, rute, waktuBerangkat, harga, status;

        GenerateTicketTask(String namaUser , String idPesan, String rute, String waktuBerangkat, String harga, String status) {
            this.namaUser  = namaUser ;
            this.idPesan = idPesan;
            this.rute = rute;
            this.waktuBerangkat = waktuBerangkat;
            this.harga = harga;
            this.status = status;
        }

        @Override
        protected File doInBackground(Void... voids) {
            return generateTicket(namaUser , idPesan, rute, waktuBerangkat, harga, status);
        }

        @Override
        protected void onPostExecute(File pdfFile) {
            super.onPostExecute(pdfFile);
            if (pdfFile != null) {
                openPdf(pdfFile);
            } else {
                Toast.makeText(CetakTiketActivity.this, "Gagal membuat tiket.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File generateTicket(String namaUser, String idPesan, String rute, String waktuBerangkat, String harga, String status) {
        String logoUrl = "http://192.168.1.17/wanderlust/admin/image/lofo_wanderlust1.png";
        File pdfFile = null;

        try {
            File directory = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Tiket");
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Gagal membuat folder unduhan.");
            }

            pdfFile = new File(directory, "Tiket_" + idPesan + ".pdf");

            File finalPdfFile = pdfFile;
            Glide.with(this)
                    .asBitmap()
                    .load(logoUrl)
                    .override(500, 250)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            try (PdfWriter writer = new PdfWriter(finalPdfFile);
                                 PdfDocument pdfDocument = new PdfDocument(writer);
                                 Document document = new Document(pdfDocument)) {

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] logoBytes = stream.toByteArray();

                                com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(
                                        com.itextpdf.io.image.ImageDataFactory.create(logoBytes))
                                        .scaleToFit(200, 100)
                                        .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER)
                                        .setMarginBottom(16);
                                document.add(logo);

                                Paragraph title = new Paragraph("Tiket Anda")
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setFontSize(18)
                                        .setFontColor(ColorConstants.BLUE)
                                        .setMarginBottom(16);
                                document.add(title);

                                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                                        .setWidth(UnitValue.createPercentValue(100))
                                        .setMarginBottom(16);

                                table.addCell("Nama:");
                                table.addCell(namaUser);

                                table.addCell("ID Pesan:");
                                table.addCell(idPesan);

                                table.addCell("Rute:");
                                table.addCell(rute);

                                table.addCell("Waktu Keberangkatan:");
                                table.addCell(waktuBerangkat);

                                table.addCell("Harga:");
                                table.addCell("Rp " + harga);

                                table.addCell("Status:");
                                table.addCell(status);

                                document.add(table);

                                Paragraph footer = new Paragraph("Terima kasih telah memesan tiket dengan kami.\n" +
                                        "Kami berharap Anda memiliki perjalanan yang menyenangkan dan aman.")
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setFontSize(14)
                                        .setFontColor(ColorConstants.GRAY)
                                        .setMarginTop(20);
                                document.add(footer);

                                // Tutup dokumen PDF
                                document.close();

                                runOnUiThread(() -> {
                                    Toast.makeText(CetakTiketActivity.this,
                                            "Tiket berhasil disimpan di: " + finalPdfFile.getPath(),
                                            Toast.LENGTH_SHORT).show();
                                    openPdf(finalPdfFile); // Buka PDF setelah selesai dibuat
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(() ->
                                        Toast.makeText(CetakTiketActivity.this,
                                                "Terjadi kesalahan saat membuat tiket: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show());
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return pdfFile;
    }

    private void openPdf(File pdfFile) {
        if (pdfFile.exists()) {
            Uri uri = FileProvider.getUriForFile(this, "com.t1co.wanderlust.provider", pdfFile);

            // Intent untuk membuka PDF di Chrome
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setPackage("com.android.chrome");

        } else {
            Toast.makeText(this, "File PDF tidak ditemukan.", Toast.LENGTH_SHORT).show();
        }
    }

}