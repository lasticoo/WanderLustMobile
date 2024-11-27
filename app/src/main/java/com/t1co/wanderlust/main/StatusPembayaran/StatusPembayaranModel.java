package com.t1co.wanderlust.main.StatusPembayaran;

public class StatusPembayaranModel {
    private String idPesan, tujuan, waktu, harga, status;

    public StatusPembayaranModel(String idPesan, String tujuan, String waktu, String harga, String status) {
        this.idPesan = idPesan;
        this.tujuan = tujuan;
        this.waktu = waktu;
        this.harga = harga;
        this.status = status;
    }

    public String getIdPesan() {
        return idPesan;
    }

    public String getTujuan() {
        return tujuan;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getHarga() {
        return harga;
    }

    public String getStatus() {
        return status;
    }
}
