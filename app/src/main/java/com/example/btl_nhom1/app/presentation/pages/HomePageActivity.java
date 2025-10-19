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
import com.example.btl_nhom1.app.presentation.common.CustomBottomNavigationView;
import com.example.btl_nhom1.app.presentation.common.CustomCategoryDrawer;

import java.util.Arrays;
import java.util.List;

public class HomePageActivity extends AppCompatActivity implements CustomBottomNavigationView.OnBottomNavigationItemClickListener {

    // Slider
    private ViewPager2 viewPager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private int currentIndex = 0;
    private final List<Integer> imageList = Arrays.asList(
            R.drawable.slider_ba,
            R.drawable.slider_bon,
            R.drawable.slider_mot,
            R.drawable.slider_hai
    );
    private static final long SLIDE_DELAY_MS = 5000L;
    //---------------------
    private CustomCategoryDrawer customCategoryDrawer;
    private CustomBottomNavigationView customBottomNav;

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


        // ---------------- Danh mục (menu trượt) - Sử dụng CustomCategoryDrawer ----------------
        customCategoryDrawer = findViewById(R.id.customCategoryDrawer); // Ánh xạ Custom View

        // ---------------- CustomBottomNavigationView ----------------
        customBottomNav = findViewById(R.id.customBottomNav);
        if (customBottomNav != null) {
            customBottomNav.setOnBottomNavigationItemClickListener(this); // Đặt listener cho bottom nav
        } else {
            Log.e("HomePageActivity", "CustomBottomNavigationView not found.");
        }
        // ---------------- End CustomBottomNavigationView ----------------
    }

    @Override
    public void onCategoryMenuClicked() {
        if (customCategoryDrawer != null) {
            customCategoryDrawer.openDrawer();
        }
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