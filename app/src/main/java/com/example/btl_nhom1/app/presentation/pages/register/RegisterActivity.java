package com.example.btl_nhom1.app.presentation.pages.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Account;
import com.example.btl_nhom1.app.domain.repository.AccountRepository;
import com.example.btl_nhom1.app.presentation.pages.login.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private TextView edtBirthDate;
    private ImageView imgCalendar;
    private Spinner spinnerGender;
    private ImageView imgAvatar;
    private Button btnChooseImage;

    // Thêm các views cần thiết cho validation
    private EditText edtLastName, edtFirstName, edtEmail, edtUsername, edtPhone, edtAddress;
    private TextInputEditText edtPassword, edtConfirmPassword;
    private Button btnRegister;

    // Activity result launcher để lấy ảnh từ gallery
    private ActivityResultLauncher<Intent> pickImageLauncher;

    // Repository để gọi API
    private AccountRepository accountRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // bind views
        edtBirthDate = findViewById(R.id.edtBirthDate);
        imgCalendar = findViewById(R.id.imgCalendar);
        spinnerGender = findViewById(R.id.spinnerGender);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnChooseImage = findViewById(R.id.btnChooseImage);

        // Thêm bind cho validation
        edtLastName = findViewById(R.id.edtLastName);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.register);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);

        // Khởi tạo repository
        accountRepository = new AccountRepository(this);

        // 1) Spinner: lấy dữ liệu từ string-array (res/values/strings.xml)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // 2) Date picker: khi click vào TextView hoặc icon calendar
        View.OnClickListener openDatePicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        };
        edtBirthDate.setOnClickListener(openDatePicker);
        imgCalendar.setOnClickListener(openDatePicker);

        // 3) Avatar picker: đăng ký ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                // Hiển thị ảnh vào ImageView
                                imgAvatar.setImageURI(selectedImageUri);
                                // Nếu cần xử lý thêm (ví dụ resize), làm ở đây
                            }
                        }
                    }
                }
        );

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryForImage();
            }
        });

        // đảm bảo ImageView bo tròn trong XML bằng drawable + clipToOutline (nếu dùng)
        // nếu muốn, bạn có thể set placeholder ở đây:
        imgAvatar.setImageResource(android.R.drawable.ic_menu_gallery);

        // 4) Xử lý nút đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    handleRegister();
                }
            }
        });
    }

    private void showDatePicker() {
        // lấy ngày hiện tại làm mặc định
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH); // 0-based
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selYear, int selMonth, int selDay) {
                        // selMonth là 0-based -> +1 cho hiển thị
                        String formatted = String.format("%02d/%02d/%04d", selDay, selMonth + 1, selYear);
                        edtBirthDate.setText(formatted);
                    }
                }, year, month, day);

        // Optionally: set giới hạn chọn ngày (ví dụ không cho chọn ngày trong tương lai)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void openGalleryForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    // Hàm validation với regex mật khẩu
    private boolean validateInputs() {
        // Validate họ
        if (edtLastName.getText().toString().trim().isEmpty()) {
            edtLastName.setError("Vui lòng nhập họ");
            edtLastName.requestFocus();
            return false;
        }

        // Validate tên
        if (edtFirstName.getText().toString().trim().isEmpty()) {
            edtFirstName.setError("Vui lòng nhập tên");
            edtFirstName.requestFocus();
            return false;
        }

        // Validate email
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return false;
        }

        // Validate tài khoản
        String username = edtUsername.getText().toString().trim();
        if (username.isEmpty()) {
            edtUsername.setError("Vui lòng nhập tài khoản");
            edtUsername.requestFocus();
            return false;
        }

        // Validate mật khẩu với regex: 8–16 ký tự, ít nhất 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt
        String password = edtPassword.getText().toString();
        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return false;
        }

        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$";
        if (!password.matches(passwordRegex)) {
            edtPassword.setError("Mật khẩu phải có 8-16 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt (@$!%*?&)");
            edtPassword.requestFocus();
            return false;
        }

        // Validate xác nhận mật khẩu
        String confirmPassword = edtConfirmPassword.getText().toString();
        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            edtConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu không khớp");
            edtConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    // Hàm xử lý đăng ký
    private void handleRegister() {
        // Disable button để tránh click nhiều lần
        btnRegister.setEnabled(false);

        // Lấy dữ liệu từ form
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String birthDate = edtBirthDate.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();

        // Gọi API đăng ký
        accountRepository.register(
                firstName,
                lastName,
                email,
                birthDate,
                gender,
                phone,
                address,
                username,
                password,
                null, // avatarBase64 (chưa xử lý)
                new AccountRepository.RegisterCallback() {
                    @Override
                    public void onSuccess(Account account, String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnRegister.setEnabled(true);
                                Toast.makeText(RegisterActivity.this,
                                        message != null ? message : "Đăng ký thành công!",
                                        Toast.LENGTH_LONG).show();

                                // Quay lại màn hình login
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnRegister.setEnabled(true);
                                Toast.makeText(RegisterActivity.this,
                                        errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accountRepository != null) {
            accountRepository.cancelAllRequests();
        }
    }
}