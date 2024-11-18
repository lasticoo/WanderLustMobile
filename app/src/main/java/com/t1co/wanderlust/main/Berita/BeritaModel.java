package com.t1co.wanderlust.main.Berita;

public class BeritaModel {
    private String judul_berita;
    private String foto_berita;
    private String konten_berita;
    private String tgl_berita;

    public BeritaModel(String judul_berita, String foto_berita, String konten_berita, String tgl_berita) {
        this.judul_berita = judul_berita;
        this.foto_berita = foto_berita;
        this.konten_berita = konten_berita;
        this.tgl_berita = tgl_berita;
    }

    public String getJudulBerita() {
        return judul_berita;
    }

    public String getFotoBerita() {
        return foto_berita;
    }

    public String getKontenBerita() {
        return konten_berita;
    }

    public String getTglBerita() {
        return tgl_berita;
    }
}
