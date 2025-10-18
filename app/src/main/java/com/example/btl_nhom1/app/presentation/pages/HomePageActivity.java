package com.example.btl_nhom1.app.presentation.pages;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.CategoryItem;
import com.example.btl_nhom1.app.presentation.adapter.ExpandableCategoryAdapter;
import com.example.btl_nhom1.app.presentation.adapter.SliderAdapter;

import java.util.Arrays;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private static final long SLIDE_DELAY_MS = 5000L;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<Integer> imageList = Arrays.asList(
            R.drawable.slider_ba,
            R.drawable.slider_bon,
            R.drawable.slider_mot,
            R.drawable.slider_hai
    );
    // Slider
    private ViewPager2 viewPager;
    private Runnable sliderRunnable;
    private int currentIndex = 0;
    //---------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ---------------- Slider ----------------
        viewPager = findViewById(R.id.viewPagerSlider);
        viewPager.setAdapter(new SliderAdapter(imageList));

        sliderRunnable = () -> {
            currentIndex = (currentIndex + 1) % imageList.size();
            viewPager.setCurrentItem(currentIndex, true);
            handler.postDelayed(sliderRunnable, SLIDE_DELAY_MS);
        };
        // start/stop handled in onResume/onPause

        ImageButton btnPrev = findViewById(R.id.btnPrev);
        ImageButton btnNext = findViewById(R.id.btnNext);

        btnPrev.setOnClickListener(v -> {
            int prevIndex = viewPager.getCurrentItem() - 1;
            if (prevIndex < 0) prevIndex = imageList.size() - 1;
            viewPager.setCurrentItem(prevIndex, true);
        });

        btnNext.setOnClickListener(v -> {
            int nextIndex = (viewPager.getCurrentItem() + 1) % imageList.size();
            viewPager.setCurrentItem(nextIndex, true);
        });
        // ---------------- End Slider ----------------


        // ---------------- Danh mục (menu trượt) ----------------
        View bottomNavLayout = findViewById(R.id.includeBottomNav);
        if (bottomNavLayout == null) {
            // Đây là một kiểm tra an toàn. Nếu includeBottomNav không tìm thấy, có vấn đề lớn hơn.
            Log.e("HomePageActivity", "ERROR: includeBottomNav not found in activity_home_page.xml");
            // Thoát hoặc thông báo lỗi để tránh crash
            return;
        }


        LinearLayout navMenu = bottomNavLayout.findViewById(R.id.navMenu);
        if (navMenu == null) {
            Log.e("HomePageActivity", "ERROR: navMenu not found in fragment_bottom_navigation.xml.");
            return;
        }

        LinearLayout menuPanel = findViewById(R.id.menuPanel);
        View overlay = findViewById(R.id.overlayBackground);
        ListView listCategory = findViewById(R.id.listCategory);

        if (menuPanel == null || overlay == null || listCategory == null) {
            Log.e("HomePageActivity", "ERROR: menuPanel, overlay or listCategory not found.");
            return;
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuPanel.getLayoutParams();
        params.width = (int) (screenWidth * 0.7);
        menuPanel.setLayoutParams(params);

        List<CategoryItem> categories = Arrays.asList(
                new CategoryItem("Nhẫn", Arrays.asList("Nhẫn Vàng", "Nhẫn Kim Cương", "Nhẫn Bạc")),
                new CategoryItem("Dây chuyền", Arrays.asList("Dây Chuyền Vàng", "Dây Chuyền Bạc")),
                new CategoryItem("Bông tai", null),
                new CategoryItem("Vòng tay", null),
                new CategoryItem("Đồng hồ", null),
                new CategoryItem("Trang sức cưới", null)
        );

        ExpandableCategoryAdapter adapter = new ExpandableCategoryAdapter(this, categories);
        listCategory.setAdapter(adapter);

        navMenu.setOnClickListener(v -> {
            menuPanel.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
            menuPanel.post(() -> {
                menuPanel.setTranslationX(menuPanel.getWidth());
                menuPanel.animate().translationX(0).setDuration(300).start();
            });
        });

        overlay.setOnClickListener(v -> {
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
        });
        // ---------------- End Danh mục ----------------
    }

    // SuKienSlider
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(sliderRunnable, SLIDE_DELAY_MS);
    }
    // ----------------
}