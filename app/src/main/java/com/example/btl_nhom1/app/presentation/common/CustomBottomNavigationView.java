package com.example.btl_nhom1.app.presentation.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.presentation.pages.home.CommingSoonActivity;
import com.example.btl_nhom1.app.presentation.pages.home.HomePageActivity;
import com.example.btl_nhom1.app.presentation.pages.login.LoginActivity;

public class CustomBottomNavigationView extends LinearLayout {

    private OnBottomNavigationItemClickListener listener;
    private TextView tvMyPNJLabel;
    private SharedPreferences sharedPreferences;

    public CustomBottomNavigationView(Context context) {
        super(context);
        init(context);
    }

    public CustomBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnBottomNavigationItemClickListener(OnBottomNavigationItemClickListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.fragment_bottom_navigation, this, true);

        sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navPromotion = findViewById(R.id.navPromotion);
        LinearLayout navMyPNJ = findViewById(R.id.navMyPNJ);
        LinearLayout navNotification = findViewById(R.id.navNotification);
        LinearLayout navConsult = findViewById(R.id.navConsult);
        LinearLayout navMenu = findViewById(R.id.navMenu);

        // Lấy TextView hiển thị tên My PNJ
        tvMyPNJLabel = findViewById(R.id.labelMyPNJ);

        // Cập nhật tên user khi khởi tạo
        updateUserName();

        // Xử lý click cho Trang chủ
        navHome.setOnClickListener(v -> {
            if (!(context instanceof HomePageActivity)) {
                Intent intent = new Intent(context, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });

        // Xử lý click cho Khuyến mại
        navPromotion.setOnClickListener(v -> {
            if (!(context instanceof CommingSoonActivity)) {
                Intent intent = new Intent(context, CommingSoonActivity.class);
                // Các flag này sẽ xóa tất cả Activity trên HomePageActivity và đưa HomePageActivity lên trên cùng
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });

        // Xử lý click cho My PNJ
        navMyPNJ.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

            if (isLoggedIn) {
                // Đăng xuất người dùng
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", false);
                editor.remove("user_name");
                editor.remove("user_id");
                editor.apply();

                Toast.makeText(context, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                // Chuyển về màn hình đăng nhập
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);

            } else {
                // Nếu chưa đăng nhập, chuyển đến LoginActivity
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            }
        });

        // Xử lý click cho Thông báo
        navNotification.setOnClickListener(v -> {
            if (!(context instanceof CommingSoonActivity)) {
                Intent intent = new Intent(context, CommingSoonActivity.class);
                // Các flag này sẽ xóa tất cả Activity trên HomePageActivity và đưa HomePageActivity lên trên cùng
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });

        // Xử lý click cho Tư vấn
        navConsult.setOnClickListener(v -> {
            if (!(context instanceof CommingSoonActivity)) {
                Intent intent = new Intent(context, CommingSoonActivity.class);
                // Các flag này sẽ xóa tất cả Activity trên HomePageActivity và đưa HomePageActivity lên trên cùng
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });

        // Xử lý click cho Danh mục
        navMenu.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryMenuClicked();
            }
        });

        // Xử lý click cho Tư vấn
        navConsult.setOnClickListener(v -> {
            if (!(context instanceof CommingSoonActivity)) {
                Intent intent = new Intent(context, CommingSoonActivity.class);
                // Các flag này sẽ xóa tất cả Activity trên HomePageActivity và đưa HomePageActivity lên trên cùng
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });
    }

    public void updateUserName() {
        if (tvMyPNJLabel == null) {
            tvMyPNJLabel = findViewById(R.id.labelMyPNJ);
        }
        if (tvMyPNJLabel == null) return;

        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            // Lấy thông tin từ SharedPreferences
            String firstName = sharedPreferences.getString("firstname", "");
            String lastName = sharedPreferences.getString("lastname", "");
            String fullName = sharedPreferences.getString("full_name", "");
            String username = sharedPreferences.getString("username", "");

            // Ưu tiên ghép lastname + firstname
            String displayName = "";
            if (!firstName.isEmpty() || !lastName.isEmpty()) {
                displayName = (lastName + " " + firstName).trim();
            } else if (!fullName.isEmpty()) {
                displayName = fullName;
            } else if (!username.isEmpty()) {
                displayName = username;
            } else {
                displayName = "Tài khoản";
            }

            tvMyPNJLabel.setText(displayName);
        } else {
            tvMyPNJLabel.setText("My PNJ");
        }
    }

    /**
     * Đăng xuất và reset tên về "My PNJ"
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        if (tvMyPNJLabel != null) {
            tvMyPNJLabel.setText("My PNJ");
        }
    }

    public interface OnBottomNavigationItemClickListener {
        void onCategoryMenuClicked();
    }
}