package com.example.politekniknegerisriwijaya;

import android.graphics.Bitmap;

public class Absensi {
    private int id;
    private int userId;
    private String tipeAbsensi; // "masuk" atau "keluar"
    private String nama;
    private String tanggal;
    private String waktu;
    private String lokasi;
    private String keterangan;
    private Bitmap foto;
    private String createdAt;

    // Constructor
    public Absensi() {
    }

    public Absensi(int userId, String tipeAbsensi, String nama, String tanggal,
                   String waktu, String lokasi, String keterangan, Bitmap foto) {
        this.userId = userId;
        this.tipeAbsensi = tipeAbsensi;
        this.nama = nama;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.lokasi = lokasi;
        this.keterangan = keterangan;
        this.foto = foto;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTipeAbsensi() {
        return tipeAbsensi;
    }

    public void setTipeAbsensi(String tipeAbsensi) {
        this.tipeAbsensi = tipeAbsensi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}