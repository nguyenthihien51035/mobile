package com.example.btl_nhom1.app.presentation.pages.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Account;
import com.example.btl_nhom1.app.domain.repository.AccountRepository;
import com.example.btl_nhom1.app.presentation.pages.home.HomePageActivity;
import com.example.btl_nhom1.app.presentation.pages.login.LoginActivity;
import com.example.btl_nhom1.app.presentation.utils.SharedPrefsUtils;

public class MyAccountActivity extends AppCompatActivity {
    private Button btnInfo;
    private Button btnChangePassword;
    private ImageView ivAvatar;
    private ImageView ivHome;
    private TextView tvUserName;
    private AccountRepository accountRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.myAccount), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo views
        btnInfo = findViewById(R.id.btnInfo);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        ivAvatar = findViewById(R.id.ivAvatar);
        ivHome = findViewById(R.id.ivHome);
        tvUserName = findViewById(R.id.tvUserName);

        // Khởi tạo AccountRepository
        accountRepository = new AccountRepository(this);

        // Load thông tin user từ API
        loadUserData();

        // Click Home để về HomePage
        ivHome.setOnClickListener(v -> {
            Intent intent = new Intent(MyAccountActivity.this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Load fragment mặc định
        if (savedInstanceState == null) {
            loadFragment(new ProfileFragment());
            updateButtonColors(btnInfo);
        }

        btnInfo.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            updateButtonColors(btnInfo);
        });

        btnChangePassword.setOnClickListener(v -> {
            loadFragment(new ChangePasswordFragment());
            updateButtonColors(btnChangePassword);
        });
    }

    private void loadUserData() {
        if (!SharedPrefsUtils.isLoggedIn(this)) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        int userId = SharedPrefsUtils.getUserId(this);

        loadFromSharedPreferences();

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin user", Toast.LENGTH_SHORT).show();
            return;
        }

        accountRepository.getAccountById(userId, new AccountRepository.AccountCallback() {
            @Override
            public void onSuccess(Account account) {
                updateUI(account);
                SharedPrefsUtils.saveUserData(MyAccountActivity.this, account);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MyAccountActivity", "API Error: " + errorMessage);
            }
        });
    }

    private void updateUI(Account account) {
        // Cập nhật tên
        String fullName = account.getFirstname() + " " + account.getLastname();
        tvUserName.setText(fullName);

        // Cập nhật mã user với format 10 số
//        String userIdFormatted = String.format("Mã: %010d", account.getId());
//        tvUserId.setText(userIdFormatted);

        // Load avatar từ drawable
        String avatarName = account.getAvatar();
        if (avatarName != null && !avatarName.isEmpty()) {
            int avatarResId = getResources().getIdentifier(avatarName, "drawable", getPackageName());
            if (avatarResId != 0) {
                ivAvatar.setImageResource(avatarResId);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar);
            }
        } else {
            ivAvatar.setImageResource(R.drawable.ic_avatar);
        }
    }

    private void loadFromSharedPreferences() {
        // Load thông tin từ SharedPreferences nếu API lỗi
        String fullName = SharedPrefsUtils.getFullName(this);
        String avatar = SharedPrefsUtils.getAvatar(this);

        tvUserName.setText(fullName);

        if (!avatar.isEmpty()) {
            int avatarResId = getResources().getIdentifier(avatar, "drawable", getPackageName());
            if (avatarResId != 0) {
                ivAvatar.setImageResource(avatarResId);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_avatar);
            }
        } else {
            ivAvatar.setImageResource(R.drawable.ic_avatar);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accountRepository != null) {
            accountRepository.cancelAllRequests();
        }
    }
}