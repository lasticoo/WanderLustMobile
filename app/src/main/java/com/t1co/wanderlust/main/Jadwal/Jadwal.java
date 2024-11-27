package com.t1co.wanderlust.main.Jadwal;
public class Jadwal {
    private String idJadwal;
    private String jurusan;
    private String tanggalJamBerangkat;
    private int harga;
    private int jumlahKursiTersedia;

    public Jadwal(String idJadwal, String jurusan, String tanggalJamBerangkat, int harga, int jumlahKursiTersedia) {
        this.idJadwal = idJadwal;
        this.jurusan = jurusan;
        this.tanggalJamBerangkat = tanggalJamBerangkat;
        this.harga = harga;
        this.jumlahKursiTersedia = jumlahKursiTersedia;
    }

    public String getIdJadwal() {
        return idJadwal;
    }

    public String getJurusan() {
        return jurusan;
    }

    public String getTanggalJamBerangkat() {
        return tanggalJamBerangkat;
    }

    public int getHarga() {
        return harga;
    }

    public int getJumlahKursiTersedia() {
        return jumlahKursiTersedia;
    }
}