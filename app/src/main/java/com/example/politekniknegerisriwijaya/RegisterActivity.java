package com.example.politekniknegerisriwijaya;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private TextInputEditText etFullName, etEmail, etUsername, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // GUNAKAN SINGLETON getInstance() - BUKAN new DatabaseHelper()
        databaseHelper = DatabaseHelper.getInstance(this);
        Log.d(TAG, "✓ DatabaseHelper singleton initialized");

        // Inisialisasi Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Tombol Register
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Navigasi ke Login
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        Log.d(TAG, "=== REGISTRATION ATTEMPT ===");
        Log.d(TAG, "Full Name: '" + fullName + "'");
        Log.d(TAG, "Email: '" + email + "'");
        Log.d(TAG, "Username: '" + username + "' (length: " + username.length() + ")");
        Log.d(TAG, "Password length: " + password.length());

        // Validasi Nama Lengkap
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Nama lengkap tidak boleh kosong");
            etFullName.requestFocus();
            return;
        }

        // Validasi Email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return;
        }

        // Validasi Username
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
            return;
        }

        if (username.length() < 4) {
            etUsername.setError("Username minimal 4 karakter");
            etUsername.requestFocus();
            return;
        }

        // Cek apakah username sudah ada
        if (databaseHelper.isUsernameExists(username)) {
            etUsername.setError("Username sudah terdaftar");
            etUsername.requestFocus();
            Toast.makeText(this, "Username '" + username + "' sudah digunakan!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Cek apakah email sudah ada
        if (databaseHelper.isEmailExists(email)) {
            etEmail.setError("Email sudah terdaftar");
            etEmail.requestFocus();
            Toast.makeText(this, "Email '" + email + "' sudah digunakan!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Validasi Password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        // Validasi Konfirmasi Password
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Konfirmasi password tidak boleh kosong");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password tidak cocok");
            etConfirmPassword.requestFocus();
            Toast.makeText(this, "Password dan konfirmasi password tidak sama!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan ke database SQLite
        boolean isInserted = databaseHelper.registerUser(fullName, email, username, password);

        if (isInserted) {
            Log.d(TAG, "✓ Registration successful for: " + username);
            Toast.makeText(this, "Registrasi berhasil! Silakan login dengan username: " + username,
                    Toast.LENGTH_LONG).show();

            // Print semua user untuk debugging
            databaseHelper.printAllUsers();

            finish(); // Kembali ke LoginActivity
        } else {
            Log.e(TAG, "✗ Registration failed for: " + username);
            Toast.makeText(this, "Registrasi gagal. Username atau email sudah terdaftar.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}