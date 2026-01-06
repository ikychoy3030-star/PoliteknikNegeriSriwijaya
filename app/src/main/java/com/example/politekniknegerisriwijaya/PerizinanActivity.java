package com.example.politekniknegerisriwijaya;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PerizinanActivity extends AppCompatActivity {

    private ImageView btnBack;
    private CardView btnSubmit;
    private TextInputEditText etNama, etTanggalMulai, etTanggalSelesai, etAlasan;
    private AutoCompleteTextView actvJenisIzin;

    private Calendar calendarMulai, calendarSelesai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perizinan);

        // Inisialisasi Views
        btnBack = findViewById(R.id.btnBack);
        btnSubmit = findViewById(R.id.btnSubmit);
        etNama = findViewById(R.id.etNama);
        actvJenisIzin = findViewById(R.id.actvJenisIzin);
        etTanggalMulai = findViewById(R.id.etTanggalMulai);
        etTanggalSelesai = findViewById(R.id.etTanggalSelesai);
        etAlasan = findViewById(R.id.etAlasan);

        // Set tint color untuk back button
        btnBack.setColorFilter(getResources().getColor(android.R.color.black));

        calendarMulai = Calendar.getInstance();
        calendarSelesai = Calendar.getInstance();

        // Setup Dropdown Jenis Izin
        setupJenisIzinDropdown();

        // Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Tanggal Mulai Picker
        etTanggalMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerMulai();
            }
        });

        // Tanggal Selesai Picker
        etTanggalSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerSelesai();
            }
        });

        // Submit Button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPerizinan();
            }
        });
    }

    private void setupJenisIzinDropdown() {
        String[] jenisIzin = {
                "Sakit",
                "Izin Pribadi",
                "Cuti",
                "Dinas Luar",
                "Keperluan Keluarga",
                "Lainnya"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                jenisIzin
        );

        actvJenisIzin.setAdapter(adapter);
    }

    private void showDatePickerMulai() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        calendarMulai.set(Calendar.YEAR, year);
                        calendarMulai.set(Calendar.MONTH, month);
                        calendarMulai.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateTanggalMulai();
                    }
                },
                calendarMulai.get(Calendar.YEAR),
                calendarMulai.get(Calendar.MONTH),
                calendarMulai.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showDatePickerSelesai() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        calendarSelesai.set(Calendar.YEAR, year);
                        calendarSelesai.set(Calendar.MONTH, month);
                        calendarSelesai.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateTanggalSelesai();
                    }
                },
                calendarSelesai.get(Calendar.YEAR),
                calendarSelesai.get(Calendar.MONTH),
                calendarSelesai.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateTanggalMulai() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etTanggalMulai.setText(dateFormat.format(calendarMulai.getTime()));
    }

    private void updateTanggalSelesai() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        etTanggalSelesai.setText(dateFormat.format(calendarSelesai.getTime()));
    }

    private void submitPerizinan() {
        // Validasi input
        String nama = etNama.getText().toString().trim();
        String jenisIzin = actvJenisIzin.getText().toString().trim();
        String tanggalMulai = etTanggalMulai.getText().toString().trim();
        String tanggalSelesai = etTanggalSelesai.getText().toString().trim();
        String alasan = etAlasan.getText().toString().trim();

        if (nama.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong");
            etNama.requestFocus();
            return;
        }

        if (jenisIzin.isEmpty()) {
            Toast.makeText(this, "Silakan pilih jenis izin", Toast.LENGTH_SHORT).show();
            actvJenisIzin.requestFocus();
            return;
        }

        if (tanggalMulai.isEmpty()) {
            Toast.makeText(this, "Silakan pilih tanggal mulai", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tanggalSelesai.isEmpty()) {
            Toast.makeText(this, "Silakan pilih tanggal selesai", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi tanggal selesai tidak boleh sebelum tanggal mulai
        if (calendarSelesai.before(calendarMulai)) {
            Toast.makeText(this, "Tanggal selesai tidak boleh sebelum tanggal mulai",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (alasan.isEmpty()) {
            etAlasan.setError("Alasan tidak boleh kosong");
            etAlasan.requestFocus();
            return;
        }

        // Proses submit perizinan
        // TODO: Simpan data ke database atau kirim ke server

        Toast.makeText(this, "Perizinan berhasil diajukan!", Toast.LENGTH_LONG).show();

        // Kembali ke dashboard
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}