package com.example.btl_nhom1.app.presentation.pages.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Account;
import com.example.btl_nhom1.app.domain.repository.AccountRepository;
import com.example.btl_nhom1.app.dto.form.ChangePasswordForm;
import com.example.btl_nhom1.app.presentation.pages.login.LoginActivity;
import com.example.btl_nhom1.app.presentation.utils.SharedPrefsUtils;
import com.example.btl_nhom1.app.presentation.utils.ValidationAccountUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordFragment extends Fragment {
    private static final String TAG = "ChangePasswordFragment";
    private TextInputLayout tilOldPassword, tilNewPassword, tilConfirmPassword;
    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnSubmitChangePassword;
    private AccountRepository accountRepository;
    private int userId;
    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        initViews(view);
        loadUserInfo();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        tilOldPassword = view.findViewById(R.id.tilOldPassword);
        tilNewPassword = view.findViewById(R.id.tilNewPassword);
        tilConfirmPassword = view.findViewById(R.id.tilConfirmPassword);

        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        btnSubmitChangePassword = view.findViewById(R.id.btnSubmitChangePassword);

        accountRepository = new AccountRepository(requireContext());
    }

    private void loadUserInfo() {
        // Sử dụng SharedPrefsUtils
        if (!SharedPrefsUtils.isLoggedIn(requireContext())) {
            Toast.makeText(requireContext(), "Phiên đăng nhập hết hạn", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        userId = SharedPrefsUtils.getUserId(requireContext());
        username = SharedPrefsUtils.getUsername(requireContext());

        if (userId == -1 || username.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        // Kiểm tra trạng thái tài khoản
//        if (!SharedPrefsUtils.isAccountActive(requireContext())) {
//            showErrorDialog("Tài khoản bị khóa", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên.");
//            btnSubmitChangePassword.setEnabled(false);
//        }

        Log.d(TAG, "User loaded - ID: " + userId + ", Username: " + username);
    }

    private void setupListeners() {
        // Clear error on typing
        etOldPassword.addTextChangedListener(createErrorClearWatcher(tilOldPassword));
        etNewPassword.addTextChangedListener(createErrorClearWatcher(tilNewPassword));
        etConfirmPassword.addTextChangedListener(createErrorClearWatcher(tilConfirmPassword));

        // Validate confirm password real-time
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilConfirmPassword.setError(null);

                String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString() : "";
                String confirmPassword = s.toString();

                if (!confirmPassword.isEmpty() && !newPassword.isEmpty()) {
                    if (!ValidationAccountUtils.isPasswordMatch(newPassword, confirmPassword)) {
                        tilConfirmPassword.setError("Mật khẩu nhập lại không khớp");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Submit button
        btnSubmitChangePassword.setOnClickListener(v -> handleChangePassword());
    }

    private TextWatcher createErrorClearWatcher(TextInputLayout layout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void handleChangePassword() {
        // Clear previous errors
        tilOldPassword.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);

        String oldPassword = etOldPassword.getText() != null ? etOldPassword.getText().toString().trim() : "";
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        // ===== VALIDATE Ở UI =====

        // 1. Validate confirmPassword (chỉ ở UI)
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Vui lòng nhập lại mật khẩu mới");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!ValidationAccountUtils.isPasswordMatch(newPassword, confirmPassword)) {
            tilConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        // 2. Validate form (currentPassword, newPassword)
        ChangePasswordForm form = new ChangePasswordForm(oldPassword, newPassword);
        ValidationAccountUtils.ValidationResult result = ValidationAccountUtils.validateChangePasswordForm(form);

        if (!result.isValid()) {
            String errorMessage = result.getFirstError();

            if (errorMessage.contains("Mật khẩu cũ")) {
                tilOldPassword.setError(errorMessage);
                etOldPassword.requestFocus();
            } else if (errorMessage.contains("Mật khẩu mới phải khác")) {
                tilNewPassword.setError(errorMessage);
                etNewPassword.requestFocus();
            } else if (errorMessage.contains("Mật khẩu mới")) {
                tilNewPassword.setError(errorMessage);
                etNewPassword.requestFocus();
            } else {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
            }

            return;
        }

        // ===== GỌI API =====

        btnSubmitChangePassword.setEnabled(false);
        btnSubmitChangePassword.setText("Đang xử lý...");

        Log.d(TAG, "Calling changePassword API for userId: " + userId);

        accountRepository.changePassword(userId, oldPassword, newPassword,
                new AccountRepository.ChangePasswordCallback() {
                    @Override
                    public void onSuccess(String message) {
                        if (getActivity() == null) return;

                        Log.d(TAG, "ChangePassword success: " + message);

                        requireActivity().runOnUiThread(() -> {
                            btnSubmitChangePassword.setEnabled(true);
                            btnSubmitChangePassword.setText("Đổi Mật Khẩu");

                            showSuccessDialogAndRelogin(newPassword);
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (getActivity() == null) return;

                        Log.e(TAG, "ChangePassword error: " + errorMessage);

                        requireActivity().runOnUiThread(() -> {
                            btnSubmitChangePassword.setEnabled(true);
                            btnSubmitChangePassword.setText("Đổi Mật Khẩu");

                            // Show error in appropriate field
                            if (errorMessage.contains("Mật khẩu hiện tại không đúng") ||
                                    errorMessage.contains("PASSWORD_IS_NOT_CORRECT")) {
                                tilOldPassword.setError("Mật khẩu hiện tại không đúng");
                                etOldPassword.requestFocus();
                            } else if (errorMessage.contains("Mật khẩu mới phải khác") ||
                                    errorMessage.contains("NEW_PASSWORD_MUST_BE_DIFFERENT")) {
                                tilNewPassword.setError("Mật khẩu mới phải khác mật khẩu cũ");
                                etNewPassword.requestFocus();
                            } else if (errorMessage.contains("không hoạt động") ||
                                    errorMessage.contains("ACCOUNT_STATUS_INVALID")) {
                                showErrorDialog("Lỗi", "Tài khoản đã bị khóa hoặc không hoạt động");
                            } else {
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }

    private void showSuccessDialogAndRelogin(String newPassword) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Thành công")
                .setMessage("Đổi mật khẩu thành công!\n\nVui lòng đăng nhập lại với mật khẩu mới.")
                .setCancelable(false)
                .setPositiveButton("Đăng nhập lại", (dialog, which) -> {
                    performAutoLogin(username, newPassword);
                })
                .show();
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void performAutoLogin(String username, String newPassword) {
        Toast.makeText(requireContext(), "Đang đăng nhập...", Toast.LENGTH_SHORT).show();

        accountRepository.login(username, newPassword, new AccountRepository.LoginCallback() {
            @Override
            public void onSuccess(Account account, String message) {
                if (getActivity() == null) return;

                requireActivity().runOnUiThread(() -> {
                    // Lưu session mới bằng SharedPrefsUtils
                    SharedPrefsUtils.saveUserData(requireContext(), account);

                    Log.d(TAG, "Auto-login success - User ID: " + account.getId());

                    Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Navigate back
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() == null) return;

                requireActivity().runOnUiThread(() -> {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Lỗi đăng nhập")
                            .setMessage("Không thể đăng nhập tự động. Vui lòng đăng nhập thủ công.\n\nLỗi: " + errorMessage)
                            .setCancelable(false)
                            .setPositiveButton("Đồng ý", (dialog, which) -> navigateToLogin())
                            .show();
                });
            }
        });
    }

    private void navigateToLogin() {
        // Đăng xuất và xóa session
        SharedPrefsUtils.logout(requireContext());

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (accountRepository != null) {
            accountRepository.cancelAllRequests();
        }
    }
}