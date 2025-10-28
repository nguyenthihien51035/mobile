package com.example.btl_nhom1.app.presentation.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Category;
import com.example.btl_nhom1.app.domain.repository.CategoryRepository;
import com.example.btl_nhom1.app.presentation.adapter.ExpandableCategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CustomCategoryDrawer extends RelativeLayout {
    private LinearLayout menuPanel;
    private View overlay;
    private ListView listCategory;
    private ProgressBar progressBar;

    private CategoryRepository categoryRepository;
    private ExpandableCategoryAdapter adapter;
    private Handler mainHandler;

    public CustomCategoryDrawer(Context context) {
        super(context);
        init(context);
    }

    public CustomCategoryDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomCategoryDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Inflate layout
        LayoutInflater.from(context).inflate(R.layout.custom_category_drawer, this, true);

        menuPanel = findViewById(R.id.menuPanel);
        overlay = findViewById(R.id.overlayBackground);
        listCategory = findViewById(R.id.listCategory);
        progressBar = findViewById(R.id.progressBar);

        mainHandler = new Handler(Looper.getMainLooper());

        // Khởi tạo Repository
        categoryRepository = new CategoryRepository();

        // Cấu hình kích thước menu panel (70% chiều rộng màn hình)
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuPanel.getLayoutParams();
        params.width = (int) (screenWidth * 0.7);
        menuPanel.setLayoutParams(params);

        // Khởi tạo adapter với danh sách rỗng
        adapter = new ExpandableCategoryAdapter(context, new ArrayList<>());
        listCategory.setAdapter(adapter);

        // Xử lý đóng menu khi click vào overlay
        overlay.setOnClickListener(v -> closeDrawer());

        // Load dữ liệu từ API
        loadCategories();
    }

    private void loadCategories() {
        // Hiển thị progress bar
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        categoryRepository.getCategories(new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                // Cập nhật UI trên main thread
                mainHandler.post(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    adapter.updateCategories(categories);
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Hiển thị lỗi trên main thread
                mainHandler.post(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // Phương thức refresh dữ liệu
    public void refreshCategories() {
        loadCategories();
    }

    public void openDrawer() {
        menuPanel.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        menuPanel.post(() -> {
            menuPanel.setTranslationX(menuPanel.getWidth());
            menuPanel.animate().translationX(0).setDuration(300).start();
        });
    }

    public void closeDrawer() {
        menuPanel.post(() -> {
            menuPanel.animate()
                    .translationX(menuPanel.getWidth())
                    .setDuration(300)
                    .withEndAction(() -> {
                        menuPanel.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                    })
                    .start();
        });
    }

    public boolean isDrawerOpen() {
        return menuPanel.getVisibility() == View.VISIBLE;
    }

    public boolean handleBackPressed() {
        if (isDrawerOpen()) {
            closeDrawer();
            return true;
        }
        return false;
    }
}