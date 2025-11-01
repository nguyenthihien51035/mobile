package com.example.btl_nhom1.app.presentation.common;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.presentation.pages.cart.CartActivity;
import com.example.btl_nhom1.app.presentation.pages.home.HomePageActivity;

public class CustomHeaderView extends LinearLayout {
    private OnSearchClickListener searchClickListener;

    public CustomHeaderView(Context context) {
        super(context);
        init(context);
    }

    public CustomHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnSearchClickListener(OnSearchClickListener listener) {
        this.searchClickListener = listener;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.fragment_header, this, true);

        ImageView imgLogo = findViewById(R.id.imgLogo);
        TextView tvBadge = findViewById(R.id.tvCartBadge);
        ImageView imgCart = findViewById(R.id.imgCart);
        EditText edtSearch = findViewById(R.id.edtSearch);

        // Click logo → quay về trang chủ
        imgLogo.setOnClickListener(v -> {
            Intent intent = new Intent(context, HomePageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        });

        // (Tuỳ chọn) Click giỏ hàng
        imgCart.setOnClickListener(v -> {
            Log.d("CustomHeaderView", "Giỏ hàng đã được nhấn!");
            Intent intent = new Intent(context, CartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        });

        // Khi người dùng bấm vào ô tìm kiếm - MỞ SEARCHACTIVITY
        edtSearch.setOnClickListener(v -> {
            // Mở SearchActivity
            Intent intent = new Intent(context, Search.class);
            context.startActivity(intent);

            // Nếu có listener, cũng gọi
            if (searchClickListener != null) {
                searchClickListener.onSearchClicked();
            }
        });

        // Đảm bảo EditText không thể nhập (chỉ click để mở màn hình tìm kiếm)
        edtSearch.setFocusable(false);
        edtSearch.setClickable(true);
    }

    public interface OnSearchClickListener {
        void onSearchClicked();
    }
}