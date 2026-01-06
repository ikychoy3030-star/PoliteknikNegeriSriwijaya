package com.example.politekniknegerisriwijaya;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AbsenKeluarActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_CAMERA_PERMISSION = 101;

    private ImageView btnBack, imgPhotoPreview;
    private LinearLayout cameraPlaceholder;
    private CardView btnTakePhoto, btnSubmit, cardPhotoContainer;
    private TextInputEditText etNama, etTanggalWaktu, etLokasi, etKeterangan;

    private Bitmap capturedPhoto;
    private Calendar calendar;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absen_keluar);

        // Inisialisasi
        databaseHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Inisialisasi Views
        btnBack = findViewById(R.id.btnBack);
        imgPhotoPreview = findViewById(R.id.imgPhotoPreview);
        cameraPlaceholder = findViewById(R.id.cameraPlaceholder);
        cardPhotoContainer = findViewById(R.id.cardPhotoContainer);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSubmit = findViewById(R.id.btnSubmit);
        etNama = findViewById(R.id.etNama);
        etTanggalWaktu = findViewById(R.id.etTanggalWaktu);
        etLokasi = findViewById(R.id.etLokasi);
        etKeterangan = findViewById(R.id.etKeterangan);

        // Set tint color untuk back button
        btnBack.setColorFilter(getResources().getColor(android.R.color.black));

        // Auto fill nama dari user yang login
        String fullName = sharedPreferences.getString("fullName", "");
        etNama.setText(fullName);

        // Set lokasi kampus (read-only)
        String lokasiKampus = "Jl. Srijaya Negara, Bukit Besar, Palembang 30139, Sumatera Selatan, Indonesia";
        etLokasi.setText(lokasiKampus);
        etLokasi.setFocusable(false);
        etLokasi.setClickable(false);
        etLokasi.setLongClickable(false);
        etLokasi.setCursorVisible(false);

        calendar = Calendar.getInstance();

        // Set current date and time as default
        updateDateTimeField();

        // Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Take Photo Button
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissionAndOpen();
            }
        });

        // Photo Container Click
        cardPhotoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermissionAndOpen();
            }
        });

        // Date Time Picker
        etTanggalWaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        // Submit Button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAbsen();
            }
        });
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "Kamera tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                capturedPhoto = (Bitmap) data.getExtras().get("data");

                // Show preview
                imgPhotoPreview.setImageBitmap(capturedPhoto);
                imgPhotoPreview.setVisibility(View.VISIBLE);
                cameraPlaceholder.setVisibility(View.GONE);

                Toast.makeText(this, "Foto berhasil diambil!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk mengambil foto",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDateTimePicker() {
        // Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Time Picker after date selected
                        showTimePicker();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        updateDateTimeField();
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDateTimeField() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        etTanggalWaktu.setText(dateFormat.format(calendar.getTime()));
    }

    private void submitAbsen() {
        // Validasi input
        String nama = etNama.getText().toString().trim();
        String tanggalWaktu = etTanggalWaktu.getText().toString().trim();
        String lokasi = etLokasi.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();

        if (capturedPhoto == null) {
            Toast.makeText(this, "Silakan ambil foto selfie terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nama.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong");
            etNama.requestFocus();
            return;
        }

        if (tanggalWaktu.isEmpty()) {
            Toast.makeText(this, "Silakan pilih tanggal dan waktu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (keterangan.isEmpty()) {
            etKeterangan.setError("Keterangan tidak boleh kosong");
            etKeterangan.requestFocus();
            return;
        }

        // Format tanggal dan waktu untuk database (YYYY-MM-DD dan HH:mm)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String tanggal = dateFormat.format(calendar.getTime());
        String waktu = timeFormat.format(calendar.getTime());

        // Simpan ke database
        boolean isInserted = databaseHelper.insertAbsensi(
                userId,
                "keluar",
                nama,
                tanggal,
                waktu,
                lokasi,
                keterangan,
                capturedPhoto
        );

        if (isInserted) {
            Toast.makeText(this, "Absen keluar berhasil disimpan!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan absen!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}