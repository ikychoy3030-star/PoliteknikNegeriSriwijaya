package com.example.politekniknegerisriwijaya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvUserName, tvAvatar;
    private ImageView btnLogout;
    private CardView cardAbsenMasuk, cardAbsenKeluar, cardPerizinan, cardRiwayat;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Inisialisasi Views
        tvUserName = findViewById(R.id.tvUserName);
        tvAvatar = findViewById(R.id.tvAvatar);
        btnLogout = findViewById(R.id.btnLogout);
        cardAbsenMasuk = findViewById(R.id.cardAbsenMasuk);
        cardAbsenKeluar = findViewById(R.id.cardAbsenKeluar);
        cardPerizinan = findViewById(R.id.cardPerizinan);
        cardRiwayat = findViewById(R.id.cardRiwayat);

        // Set tint color untuk logout button
        btnLogout.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));

        // Ambil nama user dari SharedPreferences
        String fullName = sharedPreferences.getString("fullName", "User");
        String username = sharedPreferences.getString("username", "User");

        // Tampilkan nama (prioritas fullName, jika tidak ada pakai username)
        String displayName = fullName;
        if (fullName == null || fullName.isEmpty()) {
            displayName = username;
        }

        tvUserName.setText(displayName);

        // Set avatar dengan inisial nama
        if (displayName != null && !displayName.isEmpty()) {
            tvAvatar.setText(String.valueOf(displayName.charAt(0)).toUpperCase());
        } else {
            tvAvatar.setText("U");
        }

        // Animasi untuk cards
        animateCards();

        // Tombol Logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClickAnimation(v);
                showLogoutDialog();
            }
        });

        // Card Menu Click Listeners dengan animasi
        cardAbsenMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClickAnimation(v);
                // Intent ke AbsenMasukActivity
                Intent intent = new Intent(DashboardActivity.this, AbsenMasukActivity.class);
                startActivity(intent);
            }
        });

        cardAbsenKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClickAnimation(v);
                // Intent ke AbsenKeluarActivity
                Intent intent = new Intent(DashboardActivity.this, AbsenKeluarActivity.class);
                startActivity(intent);
            }
        });

        cardPerizinan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClickAnimation(v);
                // Intent ke PerizinanActivity
                Intent intent = new Intent(DashboardActivity.this, PerizinanActivity.class);
                startActivity(intent);
            }
        });

        cardRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClickAnimation(v);
                // Intent ke RiwayatActivity
                Intent intent = new Intent(DashboardActivity.this, RiwayatActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method untuk animasi cards saat pertama kali muncul
    private void animateCards() {
        // Fade in animation untuk setiap card dengan delay
        animateCardWithDelay(cardAbsenMasuk, 100);
        animateCardWithDelay(cardAbsenKeluar, 200);
        animateCardWithDelay(cardPerizinan, 300);
        animateCardWithDelay(cardRiwayat, 400);
    }

    private void animateCardWithDelay(final View view, long delay) {
        view.setAlpha(0f);
        view.setTranslationY(50f);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(500)
                        .start();
            }
        }, delay);
    }

    // Method untuk animasi klik (scale effect)
    private void addClickAnimation(final View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.95f,  // X: dari 1.0 ke 0.95
                1.0f, 0.95f,  // Y: dari 1.0 ke 0.95
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);

        view.startAnimation(scaleAnimation);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> logout())
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void logout() {
        // Hapus status login
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();

        // Kembali ke LoginActivity
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Tampilkan dialog konfirmasi keluar
        new AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Ya", (dialog, which) -> finishAffinity())
                .setNegativeButton("Tidak", null)
                .show();
    }
}