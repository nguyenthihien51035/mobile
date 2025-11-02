package com.example.btl_nhom1.app.presentation.utils;

import android.util.Log;

import com.example.btl_nhom1.app.dto.form.RegisterForm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import lombok.Getter;

public class RegisterFormValidator {
    private static final String TAG = "FormValidator";

    // Regex patterns
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public static ValidationResult validateRegisterForm(RegisterForm form) {
        Log.d(TAG, "===== VALIDATING REGISTER FORM =====");

        List<String> errors = new ArrayList<>();

        // 1. Validate userName
        if (form.getUserName() == null || form.getUserName().trim().isEmpty()) {
            errors.add("Tên đăng nhập không được bỏ trống");
            Log.w(TAG, "userName: empty");
        } else {
            Log.d(TAG, "userName: " + form.getUserName());
        }

        // 2. Validate firstName
        if (form.getFirstName() == null || form.getFirstName().trim().isEmpty()) {
            errors.add("Tên không được bỏ trống");
            Log.w(TAG, "firstName: empty");
        } else {
            Log.d(TAG, "firstName: " + form.getFirstName());
        }

        // 3. Validate lastName
        if (form.getLastName() == null || form.getLastName().trim().isEmpty()) {
            errors.add("Họ không được bỏ trống");
            Log.w(TAG, "lastName: empty");
        } else {
            Log.d(TAG, "lastName: " + form.getLastName());
        }

        // 4. Validate email
        if (form.getEmail() == null || form.getEmail().trim().isEmpty()) {
            errors.add("Email không được bỏ trống");
            Log.w(TAG, "email: empty");
        } else if (!EMAIL_PATTERN.matcher(form.getEmail()).matches()) {
            errors.add("Email không đúng định dạng");
            Log.w(TAG, "email: invalid format - " + form.getEmail());
        } else {
            Log.d(TAG, "email: " + form.getEmail());
        }

        // 5. Validate password
        if (form.getPassword() == null || form.getPassword().trim().isEmpty()) {
            errors.add("Mật khẩu không được bỏ trống");
            Log.w(TAG, "password: empty");
        } else if (!PASSWORD_PATTERN.matcher(form.getPassword()).matches()) {
            errors.add("Mật khẩu phải có 8-16 ký tự, chứa ít nhất: 1 chữ thường, 1 chữ hoa, 1 số, 1 ký tự đặc biệt (@$!%*?&)");
            Log.w(TAG, "password: invalid pattern");
        } else {
            Log.d(TAG, "password: valid");
        }

        // Optional fields (không bắt buộc, nhưng nếu có thì validate)
        if (form.getPhone() != null && !form.getPhone().isEmpty()) {
            if (!isValidPhone(form.getPhone())) {
                errors.add("Số điện thoại không hợp lệ");
                Log.w(TAG, "phone: invalid - " + form.getPhone());
            } else {
                Log.d(TAG, "phone: " + form.getPhone());
            }
        }

        boolean isValid = errors.isEmpty();
        Log.d(TAG, "Validation result: " + (isValid ? "PASS" : "FAIL (" + errors.size() + " errors)"));

        return new ValidationResult(isValid, errors);
    }

    private static boolean isValidPhone(String phone) {
        // Số điện thoại VN: 10-11 số, bắt đầu bằng 0
        return phone.matches("^0\\d{9,10}$");
    }

    @Getter
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public String getFirstError() {
            return errors.isEmpty() ? "" : errors.get(0);
        }

        public String getAllErrors() {
            return String.join("\n", errors);
        }
    }
}
