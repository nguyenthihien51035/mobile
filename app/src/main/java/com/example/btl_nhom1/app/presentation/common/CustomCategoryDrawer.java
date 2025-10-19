package com.example.btl_nhom1.app.presentation.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.CategoryItem;
import com.example.btl_nhom1.app.presentation.adapter.ExpandableCategoryAdapter;

import java.util.Arrays;
import java.util.List;

public class CustomCategoryDrawer extends RelativeLayout {
    private LinearLayout menuPanel;
    private View overlay;
    private ListView listCategory;

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
        // Inflate layout riêng của custom view
        LayoutInflater.from(context).inflate(R.layout.custom_category_drawer, this, true);

        menuPanel = findViewById(R.id.menuPanel);
        overlay = findViewById(R.id.overlayBackground);
        listCategory = findViewById(R.id.listCategory);

        // Cấu hình kích thước menu panel (70% chiều rộng màn hình)
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuPanel.getLayoutParams();
        params.width = (int) (screenWidth * 0.7);
        menuPanel.setLayoutParams(params);

        // Dữ liệu danh mục
        List<CategoryItem> categories = Arrays.asList(
                new CategoryItem("Nhẫn +", Arrays.asList("Nhẫn Vàng", "Nhẫn Kim Cương", "Nhẫn Bạc")),
                new CategoryItem("Dây chuyền +", Arrays.asList("Dây Chuyền Vàng", "Dây Chuyền Bạc")),
                new CategoryItem("Bông tai", null),
                new CategoryItem("Vòng tay", null),
                new CategoryItem("Đồng hồ", null),
                new CategoryItem("Trang sức cưới", null)
        );

        ExpandableCategoryAdapter adapter = new ExpandableCategoryAdapter(context, categories);
        listCategory.setAdapter(adapter);

        // Xử lý đóng menu khi click vào overlay
        overlay.setOnClickListener(v -> closeDrawer());
    }

    // Phương thức để mở menu
    public void openDrawer() {
        menuPanel.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        menuPanel.post(() -> {
            menuPanel.setTranslationX(menuPanel.getWidth());
            menuPanel.animate().translationX(0).setDuration(300).start();
        });
    }

    // Phương thức để đóng menu
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

    // Kiểm tra xem drawer có đang mở không
    public boolean isDrawerOpen() {
        return menuPanel.getVisibility() == View.VISIBLE;
    }

    // Override onBackPressed để đóng drawer nếu đang mở
    // Lưu ý: gọi phương thức này từ onBackPressed của Activity
    public boolean handleBackPressed() {
        if (isDrawerOpen()) {
            closeDrawer();
            return true; // Đã xử lý sự kiện back
        }
        return false; // Không xử lý, để Activity xử lý tiếp
    }
}