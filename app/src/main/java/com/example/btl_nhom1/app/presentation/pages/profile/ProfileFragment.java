package com.example.btl_nhom1.app.presentation.pages.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.presentation.pages.login.LoginActivity;
import com.example.btl_nhom1.app.presentation.utils.SharedPrefsUtils;

public class ProfileFragment extends Fragment {
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvGender;
    private TextView tvAddress;
    private LinearLayout layoutLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_info, container, false);

        // Khởi tạo views
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvGender = view.findViewById(R.id.tvGender);
        tvAddress = view.findViewById(R.id.tvAddress);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        // Load dữ liệu user
        loadUserInfo();

        // Xử lý sự kiện đăng xuất
        layoutLogout.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    private void loadUserInfo() {
        // Lấy thông tin từ SharedPreferences
        String email = SharedPrefsUtils.getEmail(requireContext());
        String phone = SharedPrefsUtils.getPhone(requireContext());
        String gender = SharedPrefsUtils.getGender(requireContext());
        String address = SharedPrefsUtils.getAddress(requireContext());

        // Set dữ liệu lên UI
        tvEmail.setText(email);
        tvPhone.setText(phone);

        // Format giới tính
        tvGender.setText(formatGender(gender));
        tvAddress.setText(address);
    }

    private String formatGender(String gender) {
        if (gender == null) return "Chưa cập nhật";

        switch (gender.toUpperCase()) {
            case "MALE":
                return "Nam";
            case "FEMALE":
                return "Nữ";
            case "OTHER":
                return "Khác";
            default:
                return gender;
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {

        SharedPrefsUtils.logout(requireContext());

        Toast.makeText(requireContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
