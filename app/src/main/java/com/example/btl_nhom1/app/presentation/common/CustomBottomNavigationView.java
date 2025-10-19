package com.example.btl_nhom1.app.presentation.common;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.presentation.pages.CommingSoonActivity;
import com.example.btl_nhom1.app.presentation.pages.HomePageActivity;
import com.example.btl_nhom1.app.presentation.pages.LoginActivity;

public class CustomBottomNavigationView extends LinearLayout {
    private OnBottomNavigationItemClickListener listener;

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

        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navPromotion = findViewById(R.id.navPromotion);
        LinearLayout navMyPNJ = findViewById(R.id.navMyPNJ);
        LinearLayout navNotification = findViewById(R.id.navNotification);
        LinearLayout navConsult = findViewById(R.id.navConsult);
        LinearLayout navMenu = findViewById(R.id.navMenu); // Nút danh mục

        // Xử lý click cho Trang chủ
        navHome.setOnClickListener(v -> {
            // Kiểm tra nếu đang ở HomePageActivity thì không làm gì để tránh tạo Activity mới
            if (!(context instanceof HomePageActivity)) {
                Intent intent = new Intent(context, HomePageActivity.class);
                // Các flag này sẽ xóa tất cả Activity trên HomePageActivity và đưa HomePageActivity lên trên cùng
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
            // Kiểm tra nếu đang ở LoginActivity thì không làm gì để tránh tạo Activity mới
            if (!(context instanceof LoginActivity)) {
                Intent intent = new Intent(context, LoginActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

        // Xử lý click cho Danh mục (sử dụng Listener để giao tiếp với Activity)
        navMenu.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryMenuClicked();
            }
        });
    }

    // Interface để giao tiếp với Activity chứa nó, ví dụ để mở Category Drawer
    public interface OnBottomNavigationItemClickListener {
        void onCategoryMenuClicked();
    }
}
