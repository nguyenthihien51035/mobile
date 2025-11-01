package com.example.btl_nhom1.app.presentation.pages.register;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.presentation.pages.home.HomePageActivity;
import com.example.btl_nhom1.app.presentation.pages.login.LoginActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword, edtConfirmPassword, edtFirstname, edtLastname, edtPhone, edtEmail, edtAddress;
    private TextView edtBirthDate;
    private Spinner spinnerGender;
    private ImageView imgAvatar, imgCalendar;
    private Button btnChooseImage, btnRegister;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Bind views
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
        imgCalendar = findViewById(R.id.imgCalendar);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnRegister = findViewById(R.id.register);

        // Spinner giới tính
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

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

        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        btnRegister.setOnClickListener(v -> performRegister());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
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

        // Validation
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() ||
                firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("REGISTER_DEBUG", "Starting registration...");

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("firstName", firstName)
                        .addFormDataPart("lastName", lastName)
                        .addFormDataPart("email", email)
                        .addFormDataPart("dateOfBirth", birthDate)
                        .addFormDataPart("gender", gender)
                        .addFormDataPart("phone", phone)
                        .addFormDataPart("address", address)
                        .addFormDataPart("userName", username)
                        .addFormDataPart("password", password);

                // ✅ UPLOAD FILE ẢNH LÊN SERVER (nếu có chọn ảnh)
                if (selectedImageUri != null) {
                    File imageFile = getFileFromUri(selectedImageUri);
                    if (imageFile != null && imageFile.exists()) {
                        RequestBody fileBody = RequestBody.create(
                                imageFile,
                                MediaType.parse("image/*")
                        );
                        builder.addFormDataPart("avatar", imageFile.getName(), fileBody);
                        Log.d("REGISTER_DEBUG", "✅ Uploading image: " + imageFile.getName());
                    } else {
                        Log.w("REGISTER_DEBUG", "⚠️ Image file not found, using default avatar");
                    }
                } else {
                    Log.d("REGISTER_DEBUG", "ℹ️ No image selected, using default avatar");
                }

                RequestBody body = builder.build();

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("http://10.0.2.2/api/postRegister.php")
                        .post(body)
                        .build();

                okhttp3.Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "null";

                Log.d("REGISTER_RESPONSE", "Code: " + response.code());
                Log.d("REGISTER_RESPONSE", "Body: " + responseBody);

                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    } else if (response.code() == 409) {
                        // Parse error message từ server
                        try {
                            org.json.JSONObject json = new org.json.JSONObject(responseBody);
                            String message = json.optString("message", "Thông tin đã tồn tại!");
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(this, "Thông tin đã tồn tại!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại (" + response.code() + ")", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e("REGISTER_ERROR", "Exception: " + e.getMessage(), e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show());
            }
        }).start();
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

            Log.d("FILE_CONVERT", "✅ Created temp file: " + tempFile.getAbsolutePath());
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
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "avatar.jpg";
    }
}