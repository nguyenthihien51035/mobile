package com.example.btl_nhom1.app.presentation.pages.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.btl_nhom1.R;

public class AccountActivity extends AppCompatActivity { // Hoặc tên Activity/Fragment của bạn

    private Button btnInfo;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account); // Đảm bảo đây là layout chính chứa các nút và FrameLayout

        btnInfo = findViewById(R.id.btnInfo);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Hiển thị Fragment thông tin tài khoản mặc định khi Activity/Fragment được tạo
        if (savedInstanceState == null) {
            loadFragment(new AccountInfoFragment());
            updateButtonColors(btnInfo);
        }

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AccountInfoFragment());
                updateButtonColors(btnInfo);
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ChangePasswordFragment());
                updateButtonColors(btnChangePassword);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void updateButtonColors(Button activeButton) {
        // active
        if (activeButton == btnInfo) {
            btnInfo.setBackgroundResource(R.drawable.btn_border_active);
            btnInfo.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            btnInfo.setBackgroundResource(R.drawable.btn_border_inactive);
            btnInfo.setTextColor(ContextCompat.getColor(this, R.color.black));
        }

        // change password
        if (activeButton == btnChangePassword) {
            btnChangePassword.setBackgroundResource(R.drawable.btn_border_active);
            btnChangePassword.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            btnChangePassword.setBackgroundResource(R.drawable.btn_border_inactive);
            btnChangePassword.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }
}
