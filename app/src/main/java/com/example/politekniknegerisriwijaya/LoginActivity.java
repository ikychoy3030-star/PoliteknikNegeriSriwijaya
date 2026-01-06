package com.example.politekniknegerisriwijaya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private TextInputEditText etUsername, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister, tvForgotPassword, tvTestLogin;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi DatabaseHelper dan SharedPreferences
        databaseHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // DEBUG: Print all users in database
        databaseHelper.printAllUsers();

        // UNCOMMENT BARIS DIBAWAH INI HANYA SEKALI untuk reset database jika masih error
        // databaseHelper.clearAllData();
        // Toast.makeText(this, "Database telah direset. Silakan daftar ulang.", Toast.LENGTH_LONG).show();

        // Inisialisasi Views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // TEMPORARY: Test button untuk auto-fill (HAPUS NANTI)
        tvTestLogin = new TextView(this);
        tvTestLogin.setText("🔍 TEST LOGIN (Debug)");
        tvTestLogin.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        tvTestLogin.setPadding(20, 20, 20, 20);
        tvTestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLogin();
            }
        });

        // Tombol Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Navigasi ke Register
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Lupa Password
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Fitur Lupa Password belum tersedia",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // METHOD UNTUK TEST LOGIN OTOMATIS
    private void testLogin() {
        Log.d(TAG, "====================================");
        Log.d(TAG, "    TEST LOGIN - AUTO ATTEMPT");
        Log.d(TAG, "====================================");

        // Print semua user dulu
        databaseHelper.printAllUsers();

        // Reset password user admin ke 123456
        databaseHelper.resetUserPassword("admin", "123456");

        // Coba login dengan user pertama yang ada di database
        Toast.makeText(this, "Password direset. Mencoba login...", Toast.LENGTH_LONG).show();

        // Test dengan hardcoded credentials
        String testUsername = "admin";
        String testPassword = "123456";

        Log.d(TAG, "Attempting auto-login with:");
        Log.d(TAG, "Username: '" + testUsername + "'");
        Log.d(TAG, "Password: '" + testPassword + "'");

        User user = databaseHelper.loginUser(testUsername, testPassword);

        if (user != null) {
            Toast.makeText(this, "✓ Test login BERHASIL!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "✗ Test login GAGAL! Cek Logcat", Toast.LENGTH_LONG).show();
        }
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        Log.d(TAG, "=== LOGIN ATTEMPT ===");
        Log.d(TAG, "Username: '" + username + "' (length: " + username.length() + ")");
        Log.d(TAG, "Password: (length: " + password.length() + ")");

        // Validasi input
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        // Login dari database SQLite
        User user = databaseHelper.loginUser(username, password);

        if (user != null) {
            Log.d(TAG, "✓ Login successful for user: " + user.getUsername());

            // Login berhasil - Simpan session
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putInt("userId", user.getId());
            editor.putString("fullName", user.getFullName());
            editor.putString("email", user.getEmail());
            editor.putString("username", user.getUsername());
            editor.apply();

            Toast.makeText(this, "Login berhasil! Selamat datang, " + user.getFullName(),
                    Toast.LENGTH_SHORT).show();

            // Pindah ke DashboardActivity
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Login gagal
            Log.e(TAG, "✗ Login failed for username: " + username);
            Toast.makeText(this, "Username atau Password salah!", Toast.LENGTH_LONG).show();

            // Clear password field untuk keamanan
            etPassword.setText("");
            etPassword.requestFocus();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Cek apakah user sudah login
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            Log.d(TAG, "User already logged in, redirecting to Dashboard");
            // Langsung ke DashboardActivity jika sudah login
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user list untuk debugging
        Log.d(TAG, "=== LoginActivity Resumed ===");
        databaseHelper.printAllUsers();
    }
}