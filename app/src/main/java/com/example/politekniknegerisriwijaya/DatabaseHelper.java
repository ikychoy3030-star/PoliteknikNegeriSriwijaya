package com.example.politekniknegerisriwijaya;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "PolsriAbsensi.db";
    private static final int DATABASE_VERSION = 2;

    // SINGLETON INSTANCE
    private static DatabaseHelper instance;

    // Tabel Users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_FULL_NAME = "full_name";
    private static final String COL_EMAIL = "email";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_CREATED_AT = "created_at";

    // Tabel Absensi
    private static final String TABLE_ABSENSI = "absensi";
    private static final String COL_ABSEN_ID = "id";
    private static final String COL_ABSEN_USER_ID = "user_id";
    private static final String COL_TIPE_ABSEN = "tipe_absensi";
    private static final String COL_NAMA = "nama";
    private static final String COL_TANGGAL = "tanggal";
    private static final String COL_WAKTU = "waktu";
    private static final String COL_LOKASI = "lokasi";
    private static final String COL_KETERANGAN = "keterangan";
    private static final String COL_FOTO = "foto";
    private static final String COL_ABSEN_CREATED_AT = "created_at";

    // CONSTRUCTOR PRIVATE untuk Singleton
    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    // METHOD SINGLETON - getInstance
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
            Log.d(TAG, "✓ DatabaseHelper instance created");
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FULL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createUsersTable);
        Log.d(TAG, "✓ Table users created");

        String createAbsensiTable = "CREATE TABLE " + TABLE_ABSENSI + " (" +
                COL_ABSEN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ABSEN_USER_ID + " INTEGER NOT NULL, " +
                COL_TIPE_ABSEN + " TEXT NOT NULL, " +
                COL_NAMA + " TEXT NOT NULL, " +
                COL_TANGGAL + " TEXT NOT NULL, " +
                COL_WAKTU + " TEXT NOT NULL, " +
                COL_LOKASI + " TEXT, " +
                COL_KETERANGAN + " TEXT, " +
                COL_FOTO + " BLOB, " +
                COL_ABSEN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COL_ABSEN_USER_ID + ") REFERENCES " +
                TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createAbsensiTable);
        Log.d(TAG, "✓ Table absensi created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "⚠ Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ABSENSI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        Log.d(TAG, "✓ Database upgraded successfully");
    }

    // ==================== USER METHODS ====================

    public boolean registerUser(String fullName, String email, String username, String password) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            // TRIM dan LOWERCASE untuk konsistensi
            fullName = fullName.trim();
            email = email.trim().toLowerCase();
            username = username.trim().toLowerCase();
            password = password.trim();

            Log.d(TAG, "=== REGISTER USER ===");
            Log.d(TAG, "Username: '" + username + "'");
            Log.d(TAG, "Email: '" + email + "'");

            // Cek duplicate
            if (isUsernameExists(username)) {
                Log.e(TAG, "✗ Username already exists: " + username);
                return false;
            }

            if (isEmailExists(email)) {
                Log.e(TAG, "✗ Email already exists: " + email);
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(COL_FULL_NAME, fullName);
            values.put(COL_EMAIL, email);
            values.put(COL_USERNAME, username);
            values.put(COL_PASSWORD, password);

            long result = db.insert(TABLE_USERS, null, values);

            if (result != -1) {
                Log.d(TAG, "✓ User registered successfully: " + username);
                return true;
            } else {
                Log.e(TAG, "✗ User registration failed: " + username);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Error registering user: " + e.getMessage());
            return false;
        }
        // TIDAK MENUTUP DATABASE - biarkan SQLiteOpenHelper yang mengelola
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            username = username.trim().toLowerCase();

            cursor = db.query(TABLE_USERS,
                    new String[]{COL_USER_ID},
                    "LOWER(" + COL_USERNAME + ")=?",
                    new String[]{username},
                    null, null, null);

            boolean exists = cursor.getCount() > 0;
            Log.d(TAG, "Username '" + username + "' exists: " + exists);
            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking username: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) cursor.close();
            // TIDAK MENUTUP DATABASE
        }
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            email = email.trim().toLowerCase();

            cursor = db.query(TABLE_USERS,
                    new String[]{COL_USER_ID},
                    "LOWER(" + COL_EMAIL + ")=?",
                    new String[]{email},
                    null, null, null);

            boolean exists = cursor.getCount() > 0;
            Log.d(TAG, "Email '" + email + "' exists: " + exists);
            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking email: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) cursor.close();
            // TIDAK MENUTUP DATABASE
        }
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();

            String originalUsername = username;
            String originalPassword = password;

            username = username.trim().toLowerCase();
            password = password.trim();

            Log.d(TAG, "========================================");
            Log.d(TAG, "         LOGIN ATTEMPT DEBUG");
            Log.d(TAG, "========================================");
            Log.d(TAG, "Original Username: '" + originalUsername + "'");
            Log.d(TAG, "Processed Username: '" + username + "'");
            Log.d(TAG, "Original Password Length: " + originalPassword.length());
            Log.d(TAG, "Processed Password Length: " + password.length());

            Log.d(TAG, "Password Characters:");
            for (int i = 0; i < password.length(); i++) {
                Log.d(TAG, "  [" + i + "] = '" + password.charAt(i) + "' (ASCII: " + (int)password.charAt(i) + ")");
            }

            Cursor checkCursor = db.query(TABLE_USERS,
                    new String[]{COL_USERNAME, COL_PASSWORD},
                    "LOWER(" + COL_USERNAME + ")=?",
                    new String[]{username},
                    null, null, null);

            if (checkCursor.moveToFirst()) {
                String savedUsername = checkCursor.getString(0);
                String savedPassword = checkCursor.getString(1);

                Log.d(TAG, "---");
                Log.d(TAG, "User Found in Database!");
                Log.d(TAG, "Saved Username: '" + savedUsername + "'");
                Log.d(TAG, "Saved Password Length: " + savedPassword.length());
                Log.d(TAG, "Saved Password Characters:");
                for (int i = 0; i < savedPassword.length(); i++) {
                    Log.d(TAG, "  [" + i + "] = '" + savedPassword.charAt(i) + "' (ASCII: " + (int)savedPassword.charAt(i) + ")");
                }
                Log.d(TAG, "---");
                Log.d(TAG, "Password Match: " + password.equals(savedPassword));
                Log.d(TAG, "Password Equals (ignore case): " + password.equalsIgnoreCase(savedPassword));
            } else {
                Log.e(TAG, "✗ Username '" + username + "' NOT FOUND in database!");
            }
            checkCursor.close();

            cursor = db.query(TABLE_USERS,
                    null,
                    "LOWER(" + COL_USERNAME + ")=? AND " + COL_PASSWORD + "=?",
                    new String[]{username, password},
                    null, null, null);

            if (cursor.moveToFirst()) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
                user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)));

                Log.d(TAG, "✓✓✓ LOGIN SUCCESSFUL ✓✓✓");
                Log.d(TAG, "========================================");
                return user;
            } else {
                Log.e(TAG, "✗✗✗ LOGIN FAILED ✗✗✗");
                Log.e(TAG, "Query did not return any results");
                Log.e(TAG, "========================================");
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "✗ Login exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) cursor.close();
            // TIDAK MENUTUP DATABASE
        }
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_USERS,
                    null,
                    COL_USER_ID + "=?",
                    new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
                user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)));
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)));
                return user;
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user: " + e.getMessage());
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // ==================== ABSENSI METHODS ====================

    public boolean insertAbsensi(int userId, String tipeAbsensi, String nama,
                                 String tanggal, String waktu, String lokasi,
                                 String keterangan, Bitmap foto) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COL_ABSEN_USER_ID, userId);
            values.put(COL_TIPE_ABSEN, tipeAbsensi);
            values.put(COL_NAMA, nama);
            values.put(COL_TANGGAL, tanggal);
            values.put(COL_WAKTU, waktu);
            values.put(COL_LOKASI, lokasi);
            values.put(COL_KETERANGAN, keterangan);

            if (foto != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                foto.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] byteArray = stream.toByteArray();
                values.put(COL_FOTO, byteArray);
            }

            long result = db.insert(TABLE_ABSENSI, null, values);
            Log.d(TAG, "Insert absensi: " + (result != -1 ? "Success" : "Failed"));
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting absensi: " + e.getMessage());
            return false;
        }
    }

    public List<Absensi> getAbsensiByUserId(int userId) {
        List<Absensi> absensiList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();

            cursor = db.query(TABLE_ABSENSI,
                    null,
                    COL_ABSEN_USER_ID + "=?",
                    new String[]{String.valueOf(userId)},
                    null, null,
                    COL_TANGGAL + " DESC, " + COL_WAKTU + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    Absensi absensi = new Absensi();
                    absensi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_ID)));
                    absensi.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_USER_ID)));
                    absensi.setTipeAbsensi(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPE_ABSEN)));
                    absensi.setNama(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA)));
                    absensi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COL_TANGGAL)));
                    absensi.setWaktu(cursor.getString(cursor.getColumnIndexOrThrow(COL_WAKTU)));
                    absensi.setLokasi(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOKASI)));
                    absensi.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(COL_KETERANGAN)));

                    byte[] fotoBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_FOTO));
                    if (fotoBytes != null) {
                        Bitmap foto = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                        absensi.setFoto(foto);
                    }

                    absensiList.add(absensi);
                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Retrieved " + absensiList.size() + " absensi for user " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting absensi: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return absensiList;
    }

    public List<Absensi> getAbsensiByMonthYear(int userId, int month, int year) {
        List<Absensi> absensiList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String monthStr = String.format("%04d-%02d", year, month);

            cursor = db.query(TABLE_ABSENSI,
                    null,
                    COL_ABSEN_USER_ID + "=? AND " + COL_TANGGAL + " LIKE ?",
                    new String[]{String.valueOf(userId), monthStr + "%"},
                    null, null,
                    COL_TANGGAL + " DESC, " + COL_WAKTU + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    Absensi absensi = new Absensi();
                    absensi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_ID)));
                    absensi.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_USER_ID)));
                    absensi.setTipeAbsensi(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPE_ABSEN)));
                    absensi.setNama(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA)));
                    absensi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COL_TANGGAL)));
                    absensi.setWaktu(cursor.getString(cursor.getColumnIndexOrThrow(COL_WAKTU)));
                    absensi.setLokasi(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOKASI)));
                    absensi.setKeterangan(cursor.getString(cursor.getColumnIndexOrThrow(COL_KETERANGAN)));

                    byte[] fotoBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_FOTO));
                    if (fotoBytes != null) {
                        Bitmap foto = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                        absensi.setFoto(foto);
                    }

                    absensiList.add(absensi);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting absensi by month: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return absensiList;
    }

    public int getAbsensiCountByType(int userId, String tipeAbsensi, int month, int year) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String monthStr = String.format("%04d-%02d", year, month);

            cursor = db.query(TABLE_ABSENSI,
                    new String[]{"COUNT(*)"},
                    COL_ABSEN_USER_ID + "=? AND " + COL_TIPE_ABSEN + "=? AND " +
                            COL_TANGGAL + " LIKE ?",
                    new String[]{String.valueOf(userId), tipeAbsensi, monthStr + "%"},
                    null, null, null);

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error counting absensi: " + e.getMessage());
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public boolean deleteAbsensi(int absensiId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            int result = db.delete(TABLE_ABSENSI,
                    COL_ABSEN_ID + "=?",
                    new String[]{String.valueOf(absensiId)});
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting absensi: " + e.getMessage());
            return false;
        }
    }

    public int getTotalAbsensiCount(int userId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_ABSENSI,
                    new String[]{"COUNT(*)"},
                    COL_ABSEN_USER_ID + "=?",
                    new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting total count: " + e.getMessage());
            return 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // ==================== DEBUG METHODS ====================

    public void printAllUsers() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_USERS, null, null, null, null, null, null);

            Log.d(TAG, "========================================");
            Log.d(TAG, "       ALL USERS IN DATABASE");
            Log.d(TAG, "========================================");

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME));
                    String password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
                    String fullName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME));

                    Log.d(TAG, "User ID: " + id);
                    Log.d(TAG, "  Name: " + fullName);
                    Log.d(TAG, "  Username: '" + username + "'");
                    Log.d(TAG, "  Password: '" + password + "'");
                    Log.d(TAG, "  Email: '" + email + "'");
                    Log.d(TAG, "---");
                } while (cursor.moveToNext());
                Log.d(TAG, "Total users: " + cursor.getCount());
            } else {
                Log.d(TAG, "⚠ No users in database");
            }
            Log.d(TAG, "========================================");
        } catch (Exception e) {
            Log.e(TAG, "Error printing users: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public void clearAllData() {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_ABSENSI, null, null);
            db.delete(TABLE_USERS, null, null);
            Log.d(TAG, "✓ All data cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing data: " + e.getMessage());
        }
    }

    public boolean resetUserPassword(String username, String newPassword) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            username = username.trim().toLowerCase();
            newPassword = newPassword.trim();

            ContentValues values = new ContentValues();
            values.put(COL_PASSWORD, newPassword);

            int rows = db.update(TABLE_USERS,
                    values,
                    "LOWER(" + COL_USERNAME + ")=?",
                    new String[]{username});

            if (rows > 0) {
                Log.d(TAG, "✓ Password reset for user: " + username);
                return true;
            } else {
                Log.e(TAG, "✗ User not found: " + username);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resetting password: " + e.getMessage());
            return false;
        }
    }
}