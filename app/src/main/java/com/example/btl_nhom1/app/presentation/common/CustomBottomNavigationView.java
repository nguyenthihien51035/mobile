package com.example.btl_nhom1.app.presentation.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.presentation.pages.comingsoon.ComingSoonActivity;
import com.example.btl_nhom1.app.presentation.pages.home.HomePageActivity;
import com.example.btl_nhom1.app.presentation.pages.login.LoginActivity;
import com.example.btl_nhom1.app.presentation.pages.profile.MyAccountActivity;

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

        navMyPNJ.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

            if (isLoggedIn) {
                // Nếu đã đăng nhập, chuyển đến trang MyAccountActivity
                Intent intent = new Intent(context, MyAccountActivity.class);
                context.startActivity(intent);

            } else {
                // Nếu chưa đăng nhập, chuyển đến LoginActivity
                if (!(context instanceof LoginActivity)) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        // Xử lý click cho Danh mục
        navMenu.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryMenuClicked();
            }
        });

        // Xử lý click cho các mục khác
        View.OnClickListener comingSoonClickListener = v -> {
            if (!(context instanceof ComingSoonActivity)) {
                Intent intent = new Intent(context, ComingSoonActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        };
        navPromotion.setOnClickListener(comingSoonClickListener);
        navNotification.setOnClickListener(comingSoonClickListener);
        navConsult.setOnClickListener(comingSoonClickListener);
    }

    /**
     * Cập nhật tên người dùng trên bottom navigation
     * Gọi method này sau khi đăng nhập thành công hoặc khi Activity resume
     */
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