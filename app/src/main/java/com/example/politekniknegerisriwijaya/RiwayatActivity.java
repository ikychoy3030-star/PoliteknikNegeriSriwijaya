package com.example.politekniknegerisriwijaya;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class RiwayatActivity extends AppCompatActivity {

    private ImageView btnBack;
    private AutoCompleteTextView actvFilterBulan;
    private TextView tvHadirCount, tvIzinCount, tvAlpaCount;
    private RecyclerView rvRiwayat;
    private LinearLayout emptyState;

    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private RiwayatAdapter adapter;
    private int userId;
    private int selectedMonth;
    private int selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        // Inisialisasi
        databaseHelper = DatabaseHelper.getInstance(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Get current month and year
        Calendar calendar = Calendar.getInstance();
        selectedMonth = calendar.get(Calendar.MONTH) + 1; // 1-12
        selectedYear = calendar.get(Calendar.YEAR);

        // Inisialisasi Views
        btnBack = findViewById(R.id.btnBack);
        actvFilterBulan = findViewById(R.id.actvFilterBulan);
        tvHadirCount = findViewById(R.id.tvHadirCount);
        tvIzinCount = findViewById(R.id.tvIzinCount);
        tvAlpaCount = findViewById(R.id.tvAlpaCount);
        rvRiwayat = findViewById(R.id.rvRiwayat);
        emptyState = findViewById(R.id.emptyState);

        // Set tint color untuk back button
        btnBack.setColorFilter(getResources().getColor(android.R.color.black));

        // Setup Filter Bulan Dropdown
        setupFilterBulan();

        // Setup RecyclerView
        setupRecyclerView();

        // Back Button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load Data pertama kali
        loadRiwayatData();
    }

    private void setupFilterBulan() {
        // Generate bulan options (6 bulan terakhir)
        String[] bulanOptions = new String[12];
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 12; i++) {
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            bulanOptions[i] = getBulanName(month) + " " + year;
            calendar.add(Calendar.MONTH, -1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                bulanOptions
        );

        actvFilterBulan.setAdapter(adapter);
        actvFilterBulan.setText(bulanOptions[0], false);

        actvFilterBulan.setOnItemClickListener((parent, view, position, id) -> {
            // Parse selected month and year
            String selected = bulanOptions[position];
            String[] parts = selected.split(" ");
            selectedMonth = getBulanNumber(parts[0]);
            selectedYear = Integer.parseInt(parts[1]);

            // Reload data
            loadRiwayatData();
        });
    }

    private String getBulanName(int month) {
        String[] bulanNames = {
                "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };
        return bulanNames[month - 1];
    }

    private int getBulanNumber(String bulanName) {
        String[] bulanNames = {
                "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };
        for (int i = 0; i < bulanNames.length; i++) {
            if (bulanNames[i].equals(bulanName)) {
                return i + 1;
            }
        }
        return 1;
    }

    private void setupRecyclerView() {
        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RiwayatAdapter(new java.util.ArrayList<>());
        rvRiwayat.setAdapter(adapter);

        // Set click listener untuk detail
        adapter.setOnItemClickListener(absensi -> {
            // TODO: Tampilkan detail absensi
            Toast.makeText(this, "Detail: " + absensi.getTipeAbsensi(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadRiwayatData() {
        // Load riwayat dari database berdasarkan bulan dan tahun
        List<Absensi> absensiList = databaseHelper.getAbsensiByMonthYear(
                userId, selectedMonth, selectedYear);

        // Update RecyclerView
        adapter.updateData(absensiList);

        // Hitung statistik
        int hadirCount = 0;
        int izinCount = 0;
        int alpaCount = 0;

        for (Absensi absensi : absensiList) {
            if (absensi.getTipeAbsensi().equals("masuk")) {
                hadirCount++;
            }
            // Note: Untuk izin dan alpa, nanti bisa ditambahkan tabel terpisah
            // atau logic tambahan
        }

        // Update UI counts
        tvHadirCount.setText(String.valueOf(hadirCount));
        tvIzinCount.setText("0"); // TODO: Implement izin logic
        tvAlpaCount.setText("0"); // TODO: Implement alpa logic

        // Show/Hide empty state
        if (absensiList.isEmpty()) {
            rvRiwayat.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            rvRiwayat.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data saat kembali ke activity ini
        loadRiwayatData();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}