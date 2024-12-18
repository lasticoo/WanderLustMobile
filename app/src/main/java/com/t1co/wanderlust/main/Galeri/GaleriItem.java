package com.t1co.wanderlust.main.Galeri;

public class GaleriItem {
    private final String nama_foto;
    private final String keterangan_foto;

    public GaleriItem(String imageUrl, String keterangan_foto) {
        this.nama_foto = imageUrl;
        this.keterangan_foto = keterangan_foto;
    }

    public String getNama_foto() {
        return nama_foto;
    }

    public String getKeterangan_foto() {
        return keterangan_foto;
    }
}

