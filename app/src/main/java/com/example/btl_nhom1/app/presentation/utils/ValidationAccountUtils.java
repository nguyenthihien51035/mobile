package com.example.btl_nhom1.app.presentation.utils;

import com.example.btl_nhom1.app.dto.form.ChangePasswordForm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ValidationAccountUtils {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$"
    );

    public static ValidationResult validateChangePasswordForm(ChangePasswordForm form) {
        List<String> errors = new ArrayList<>();

        // Check currentPassword
        if (form.getCurrentPassword() == null || form.getCurrentPassword().trim().isEmpty()) {
            errors.add("Mật khẩu cũ không được bỏ trống");
        } else if (!PASSWORD_PATTERN.matcher(form.getCurrentPassword()).matches()) {
            errors.add("Mật khẩu cũ không đúng định dạng");
        }

        // Check newPassword
        if (form.getNewPassword() == null || form.getNewPassword().trim().isEmpty()) {
            errors.add("Mật khẩu mới không được bỏ trống");
        } else if (!PASSWORD_PATTERN.matcher(form.getNewPassword()).matches()) {
            errors.add("Mật khẩu mới phải có 8-16 ký tự, chứa ít nhất: 1 chữ thường, 1 chữ hoa, 1 số, 1 ký tự đặc biệt (@$!%*?&)");
        }

        // Check new password different from current
        if (form.getCurrentPassword() != null && form.getNewPassword() != null &&
                form.getCurrentPassword().equals(form.getNewPassword())) {
            errors.add("Mật khẩu mới phải khác mật khẩu cũ");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static boolean isPasswordMatch(String password, String confirmPassword) {
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return false;
        }
        return password != null && password.equals(confirmPassword);
    }

    public static boolean isValidPasswordFormat(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Check password requirements individually
     */
    public static class PasswordRequirements {
        public boolean hasLength;
        public boolean hasLowercase;
        public boolean hasUppercase;
        public boolean hasDigit;
        public boolean hasSpecial;

        public PasswordRequirements(String password) {
            if (password != null) {
                this.hasLength = password.length() >= 8 && password.length() <= 16;
                this.hasLowercase = password.matches(".*[a-z].*");
                this.hasUppercase = password.matches(".*[A-Z].*");
                this.hasDigit = password.matches(".*\\d.*");
                this.hasSpecial = password.matches(".*[@$!%*?&].*");
            } else {
                this.hasLength = false;
                this.hasLowercase = false;
                this.hasUppercase = false;
                this.hasDigit = false;
                this.hasSpecial = false;
            }
        }

        public boolean isAllValid() {
            return hasLength && hasLowercase && hasUppercase && hasDigit && hasSpecial;
        }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getFirstError() {
            return errors.isEmpty() ? null : errors.get(0);
        }

        public String getAllErrors() {
            return String.join("\n", errors);
        }
    }
}
