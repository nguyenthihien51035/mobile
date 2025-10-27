package com.example.btl_nhom1.app.presentation.pages.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Account;
import com.example.btl_nhom1.app.domain.repository.AccountRepository;
import com.example.btl_nhom1.app.presentation.pages.home.HomePageActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private TextView tvBack, tvForgotPassword, tvSignUp, tvHomePage;
    private TextInputEditText etAccount, etPassword;
    private TextInputLayout tilAccount, tilPassword;
    private MaterialButton btnLogin;

    private AccountRepository accountRepository;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();

        accountRepository = new AccountRepository(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
    }

    private void initViews() {
        tvBack = findViewById(R.id.tvBack);
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        tilAccount = findViewById(R.id.tilAccount);
        tilPassword = findViewById(R.id.tilPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvHomePage = findViewById(R.id.tvHomePage);
    }

    private void setupClickListeners() {
        // Quay lại trang trước đó
        tvBack.setOnClickListener(v -> onBackPressed());

        // Đăng nhập
        btnLogin.setOnClickListener(v -> handleLogin());

        // Quên mật khẩu
        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng quên mật khẩu đang phát triển", Toast.LENGTH_SHORT).show());

        // Đăng ký
        tvSignUp.setOnClickListener(v ->
                Toast.makeText(this, "Chuyển đến trang đăng ký (chưa làm)", Toast.LENGTH_SHORT).show());

        // Trang chủ
        tvHomePage.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void handleLogin() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(account, password)) return;
        performLogin(account, password);
    }

    private boolean validateInput(String account, String password) {
        boolean isValid = true;

        // Regex tài khoản: chỉ cho phép chữ, số, ., _, - (4–30 ký tự)
        String usernameRegex = "^[a-zA-Z0-9._-]{4,30}$";

        // Regex mật khẩu: 8–16 ký tự, ít nhất 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";

        if (account.isEmpty()) {
            tilAccount.setError("Tài khoản không được bỏ trống");
            isValid = false;
        } else if (!account.matches(usernameRegex)) {
            tilAccount.setError("Tài khoản phải có 4-30 ký tự, chỉ chứa chữ, số, dấu chấm, gạch dưới hoặc gạch ngang");
            isValid = false;
        } else {
            tilAccount.setError(null);
        }

        if (password.isEmpty()) {
            tilPassword.setError("Mật khẩu không được bỏ trống");
            isValid = false;
        } else if (!password.matches(passwordRegex)) {
            tilPassword.setError("Mật khẩu phải có 8-16 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt (@$!%*?&)");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        return isValid;
    }

    private void performLogin(String account, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        accountRepository.login(account, password, new AccountRepository.LoginCallback() {
            @Override
            public void onSuccess(Account userData, String message) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                    saveUserData(userData);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    navigateToHome();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng nhập");
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void saveUserData(Account account) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("user_id", account.getUserId());
        editor.putString("username", account.getUsername());
        editor.putString("email", account.getEmail());
        editor.putString("full_name", account.getFullName());
        editor.putString("firstname", account.getFirstname());
        editor.putString("lastname", account.getLastname());
        editor.putString("phone", account.getPhone());
        editor.putString("address", account.getAddress());
        editor.putString("gender", account.getGender());
        editor.putString("avatar", account.getAvatar());
        editor.putString("token", account.getToken());
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("refresh_user", true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accountRepository != null) {
            accountRepository.cancelAllRequests();
        }
    }
}
