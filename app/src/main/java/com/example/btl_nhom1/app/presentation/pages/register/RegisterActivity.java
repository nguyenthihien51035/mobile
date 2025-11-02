package com.example.btl_nhom1.app.presentation.pages.register;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Account;
import com.example.btl_nhom1.app.domain.repository.AccountRepository;
import com.example.btl_nhom1.app.dto.form.RegisterForm;
import com.example.btl_nhom1.app.presentation.pages.home.HomePageActivity;
import com.example.btl_nhom1.app.presentation.pages.login.LoginActivity;
import com.example.btl_nhom1.app.presentation.utils.RegisterFormValidator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword, edtConfirmPassword, edtFirstname, edtLastname, edtPhone, edtEmail, edtAddress;
    private TextView edtBirthDate;
    private Spinner spinnerGender;
    private ImageView imgAvatar;
    private Button btnChooseImage, btnRegister;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtFirstname = findViewById(R.id.edtFirstName);
        edtLastname = findViewById(R.id.edtLastName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        spinnerGender = findViewById(R.id.spinnerGender);
        imgAvatar = findViewById(R.id.imgAvatar);
        ImageView imgCalendar = findViewById(R.id.imgCalendar);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnRegister = findViewById(R.id.register);

        // Spinner giới tính
        setupGenderSpinner();
        String userGender = "";
        setGenderSelection(userGender);

        // Date picker
        View.OnClickListener openDatePicker = v -> showDatePicker();
        edtBirthDate.setOnClickListener(openDatePicker);
        imgCalendar.setOnClickListener(openDatePicker);

        // ActivityResultLauncher để chọn ảnh
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            imgAvatar.setImageURI(selectedImageUri);
                            Log.d("REGISTER_AVATAR", "Selected image: " + selectedImageUri);
                        }
                    }
                }
        );

        btnChooseImage.setOnClickListener(v -> openGalleryForImage());

        // Navigation
        findViewById(R.id.tvHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomePageActivity.class));
            finish();
        });

        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.tvBack).setOnClickListener(v -> onBackPressed());

        btnRegister.setOnClickListener(v -> performRegister());
    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
    }

    private String mapGenderToCode(String genderText) {
        switch (genderText) {
            case "Nam":
                return "MALE";
            case "Nữ":
                return "FEMALE";
            case "Khác":
                return "OTHER";
            default:
                return "";
        }
    }

    private void setGenderSelection(String genderCode) {
        int position = 0;
        switch (genderCode) {
            case "MALE":
                break;
            case "FEMALE":
                position = 1;
                break;
            case "OTHER":
                position = 2;
                break;
        }
        spinnerGender.setSelection(position);
    }

    private void onSaveClicked() {
        String selectedGender = spinnerGender.getSelectedItem().toString();
        String genderCode = mapGenderToCode(selectedGender);
        Log.d("GENDER", "Giới tính chọn: " + selectedGender + " Code gửi API: " + genderCode);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("DefaultLocale") DatePickerDialog dialog = new DatePickerDialog(
                this,
                (DatePicker view, int y, int m, int d) ->
                        edtBirthDate.setText(String.format("%02d/%02d/%04d", d, m + 1, y)),
                year, month, day);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void openGalleryForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void performRegister() {
        Log.d("REGISTER_DEBUG", "===== STARTING REGISTRATION =====");

        // ===== LẤY DỮ LIỆU TỪ FORM =====
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String firstName = edtFirstname.getText().toString().trim();
        String lastName = edtLastname.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String birthDate = edtBirthDate.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();

        // ===== VALIDATE MẬT KHẨU KHỚP =====
        if (!password.equals(confirmPassword)) {
            Log.w("REGISTER_DEBUG", "Password mismatch");
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ===== TẠO REGISTER FORM =====
        RegisterForm form = new RegisterForm(
                username,
                firstName,
                lastName,
                birthDate,
                gender,
                phone,
                address,
                email,
                password,
                null
        );

        // ===== VALIDATE FORM =====
        RegisterFormValidator.ValidationResult validation = RegisterFormValidator.validateRegisterForm(form);

        if (!validation.isValid()) {
            Log.w("REGISTER_DEBUG", "Validation failed:");
            for (String error : validation.getErrors()) {
                Log.w("REGISTER_DEBUG", "  - " + error);
            }

            // Hiển thị lỗi đầu tiên hoặc tất cả lỗi
            Toast.makeText(this, validation.getFirstError(), Toast.LENGTH_LONG).show();
            // Hoặc hiển thị tất cả: validation.getAllErrors()
            return;
        }

        Log.d("REGISTER_DEBUG", "Validation passed");

        // ===== XỬ LÝ FILE ẢNH =====
        File avatarFile = null;
        if (selectedImageUri != null) {
            Log.d("REGISTER_DEBUG", "Processing avatar image...");
            avatarFile = getFileFromUri(selectedImageUri);

            if (avatarFile != null && avatarFile.exists()) {
                Log.d("REGISTER_DEBUG", "Avatar ready: " + avatarFile.getName() +
                        " (" + avatarFile.length() + " bytes)");
            } else {
                Log.w("REGISTER_DEBUG", "Failed to convert URI to file");
            }
        } else {
            Log.d("REGISTER_DEBUG", "No avatar selected");
        }

        // ===== GỌI API =====
        Log.d("REGISTER_DEBUG", "Calling repository.register()...");

        AccountRepository repository = new AccountRepository(this);
        repository.register(form, avatarFile, new AccountRepository.RegisterCallback() {
            @Override
            public void onSuccess(Account account, String message) {
                Log.d("REGISTER_DEBUG", "=====  SUCCESS =====");
                Log.d("REGISTER_DEBUG", "Message: " + message);
                Log.d("REGISTER_DEBUG", "ID: " + account.getId());
                Log.d("REGISTER_DEBUG", "Username: " + account.getUsername());
                Log.d("REGISTER_DEBUG", "Avatar: " + account.getAvatar());

                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                    // Chuyển sang login
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("REGISTER_DEBUG", "===== FAILED =====");
                Log.e("REGISTER_DEBUG", "Error: " + errorMessage);

                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Chuyển Uri thành File để upload
     */
    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Log.e("FILE_CONVERT", "Cannot open input stream");
                return null;
            }

            // Tạo file tạm trong cache directory
            String fileName = getFileNameFromUri(uri);
            File tempFile = new File(getCacheDir(), fileName);

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            Log.d("FILE_CONVERT", "Created temp file: " + tempFile.getAbsolutePath());
            return tempFile;

        } catch (Exception e) {
            Log.e("FILE_CONVERT", "Error converting Uri to File: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Lấy tên file từ Uri
     */
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
