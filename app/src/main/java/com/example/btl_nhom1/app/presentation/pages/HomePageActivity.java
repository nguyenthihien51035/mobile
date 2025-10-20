package com.example.btl_nhom1.app.presentation.pages;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Product;
import com.example.btl_nhom1.app.domain.repository.ProductRepository;
import com.example.btl_nhom1.app.presentation.adapter.SliderAdapter;
import com.example.btl_nhom1.app.presentation.common.CustomBottomNavigationView;
import com.example.btl_nhom1.app.presentation.common.CustomCategoryDrawer;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HomePageActivity extends AppCompatActivity implements CustomBottomNavigationView.OnBottomNavigationItemClickListener {

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

    // Views
    private CustomCategoryDrawer customCategoryDrawer;
    private CustomBottomNavigationView customBottomNav;
    private LinearLayout latestProductContainer;
    private LinearLayout topSellingProductContainer;

    // Repository
    private ProductRepository productRepository;

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

        // Khởi tạo Repository
        productRepository = new ProductRepository(this);

        // Khởi tạo views
        initViews();

        // Thiết lập slider
        setupSlider();

        // Gọi API
        fetchLatestProducts();
        fetchTopSellingProducts();

        // Xử lý nút BackCate
        setupBackPressHandler();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Nếu drawer đang mở, đóng drawer
                if (customCategoryDrawer != null && customCategoryDrawer.isDrawerOpen()) {
                    customCategoryDrawer.closeDrawer();
                } else {
                    // Nếu không, thoát activity
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void initViews() {
        View latestInclude = findViewById(R.id.includeLatest);
        View topSellingInclude = findViewById(R.id.includeTopSelling);

        topSellingProductContainer = topSellingInclude.findViewById(R.id.productContainer);
        latestProductContainer = latestInclude.findViewById(R.id.productContainer);

        // CustomCategoryDrawer
        customCategoryDrawer = findViewById(R.id.customCategoryDrawer);

        // CustomBottomNavigationView
        customBottomNav = findViewById(R.id.customBottomNav);
        if (customBottomNav != null) {
            customBottomNav.setOnBottomNavigationItemClickListener(this);
        } else {
            Log.e("HomePageActivity", "CustomBottomNavigationView not found.");
        }
    }

    private void setupSlider() {
        viewPager = findViewById(R.id.viewPagerSlider);
        viewPager.setAdapter(new SliderAdapter(imageList));

        sliderRunnable = () -> {
            currentIndex = (currentIndex + 1) % imageList.size();
            viewPager.setCurrentItem(currentIndex, true);
            handler.postDelayed(sliderRunnable, SLIDE_DELAY_MS);
        };

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
    }

    @Override
    public void onCategoryMenuClicked() {
        if (customCategoryDrawer != null) {
            customCategoryDrawer.openDrawer();
        }
    }


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productRepository != null) {
            productRepository.cancelAllRequests();
        }
    }

    // Call API thông qua Repository
    private void fetchLatestProducts() {
        productRepository.fetchLatestProducts(new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                displayProducts(products, latestProductContainer);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(HomePageActivity.this, "Lỗi latest: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTopSellingProducts() {
        productRepository.fetchTopSellingProducts(new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                displayProducts(products, topSellingProductContainer);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(HomePageActivity.this, "Lỗi top selling: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void displayProducts(List<Product> products, LinearLayout container) {
        container.removeAllViews();

        for (int i = 0; i < products.size(); i += 2) {
            LinearLayout row = createProductRow();

            View productView1 = createProductView(products.get(i));
            row.addView(productView1);

            if (i + 1 < products.size()) {
                View productView2 = createProductView(products.get(i + 1));
                row.addView(productView2);
            } else {
                View emptyView = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                emptyView.setLayoutParams(params);
                row.addView(emptyView);
            }

            container.addView(row);
        }
    }

    private LinearLayout createProductRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        row.setLayoutParams(params);
        row.setWeightSum(2.0f);
        return row;
    }

    private View createProductView(Product product) {
        // Inflate layout từ XML
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_product, null);

        // Set layout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        int margin = (int) (4 * getResources().getDisplayMetrics().density);
        params.setMargins(margin, margin, margin, margin);
        view.setLayoutParams(params);

        // Bind dữ liệu
        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        TextView tvSold = view.findViewById(R.id.tvSold);

        // Set tên sản phẩm
        String displayName = product.getDisplayName() != null ? product.getDisplayName() : product.getName();
        tvProductName.setText(displayName);

        // Format giá tiền
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String priceFormatted = formatter.format(product.getPrice()) + "đ";
        tvPrice.setText(priceFormatted);

        // Set số lượng đã bán - ẨN nếu = 0
        if (product.getSoldQuantity() > 0) {
            tvSold.setText(product.getSoldQuantity() + " đã bán");
            tvSold.setVisibility(View.VISIBLE);
        } else {
            tvSold.setVisibility(View.GONE);
        }

        // Load hình ảnh từ drawable
        String imageName = product.getPrimaryImageUrl();
        int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());

        if (imageResId != 0) {
            imgProduct.setImageResource(imageResId);
        } else {
            imgProduct.setImageResource(R.drawable.nullimage);
        }

        // Set click listener
        view.setOnClickListener(v -> {
            Toast.makeText(HomePageActivity.this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Mở trang chi tiết sản phẩm
        });

        return view;
    }
}