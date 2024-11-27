package com.t1co.wanderlust.main.Pemesanan;

public class Pemesanan {
    private int id_jadwal;
    private int id_user;
    private String no_kursi;
    private String status;
    private String email;
    private String keberangkatan;
    private String destinasi;
    private String tgl_berangkat;

    public Pemesanan(int id_jadwal, int id_user, String no_kursi, String status, String email, String keberangkatan, String destinasi, String tgl_berangkat) {
        this.id_jadwal = id_jadwal;
        this.id_user = id_user;
        this.no_kursi = no_kursi;
        this.status = status;
        this.email = email;
        this.keberangkatan = keberangkatan;
        this.destinasi = destinasi;
        this.tgl_berangkat = tgl_berangkat;
    }

    public int getId_jadwal() {
        return id_jadwal;
    }

    public int getId_user() {
        return id_user;
    }

    public String getNo_kursi() {
        return no_kursi;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getKeberangkatan() {
        return keberangkatan;
    }

    public String getDestinasi() {
        return destinasi;
    }

    public String getTgl_berangkat() {
        return tgl_berangkat;
    }
}