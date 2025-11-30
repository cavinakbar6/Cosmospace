package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

// Import semua Fragment
import com.example.myapplication.fragments.ApodFragment;
import com.example.myapplication.fragments.HomeFragment;
import com.example.myapplication.fragments.NeoTrackerFragment;
import com.example.myapplication.fragments.SolarSystemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // Variabel Global untuk Bottom Navigation
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inisialisasi View
        bottomNav = findViewById(R.id.bottom_navigation);

        // 2. Atur Listener untuk Klik Menu Bawah
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_apod) {
                selectedFragment = new ApodFragment();
            } else if (itemId == R.id.nav_neo) {
                selectedFragment = new NeoTrackerFragment();
            } else if (itemId == R.id.nav_solar_system) {
                selectedFragment = new SolarSystemFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });

        // 3. LOGIKA NAVIGASI PINTAR (Dari Kuis Explore)
        // Cek apakah Activity ini dibuka dengan "pesan khusus" dari Kuis?
        if (getIntent().hasExtra("TARGET_FRAGMENT")) {
            String target = getIntent().getStringExtra("TARGET_FRAGMENT");

            // Arahkan ke tab yang sesuai berdasarkan hasil kuis
            if ("nav_apod".equals(target)) {
                bottomNav.setSelectedItemId(R.id.nav_apod);
            } else if ("nav_neo".equals(target)) {
                bottomNav.setSelectedItemId(R.id.nav_neo);
            } else if ("nav_solar_system".equals(target)) {
                bottomNav.setSelectedItemId(R.id.nav_solar_system);
            } else {
                // Jika target tidak dikenali, buka Home
                bottomNav.setSelectedItemId(R.id.nav_home);
            }
        }
        // 4. Default: Jika aplikasi baru dibuka biasa, tampilkan Home
        else if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    // --- Metode Helper untuk Mengganti Fragment di Layar ---
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // --- Metode Publik untuk Dipanggil dari HomeFragment ---
    // Ini dipakai saat user klik kartu di halaman Home
    public void loadFragmentFromHome(int itemId) {
        if (bottomNav != null) {
            // Ini akan memicu listener di atas dan mengganti fragment secara otomatis
            bottomNav.setSelectedItemId(itemId);
        }
    }

    // --- Mengatasi masalah Intent saat Activity sudah berjalan (SingleTop) ---
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Cek lagi jika ada instruksi navigasi baru
        if (intent.hasExtra("TARGET_FRAGMENT")) {
            String target = intent.getStringExtra("TARGET_FRAGMENT");
            if ("nav_apod".equals(target)) bottomNav.setSelectedItemId(R.id.nav_apod);
            else if ("nav_neo".equals(target)) bottomNav.setSelectedItemId(R.id.nav_neo);
            else if ("nav_solar_system".equals(target)) bottomNav.setSelectedItemId(R.id.nav_solar_system);
        }
    }
}