package com.example.btl_nhom1.app.presentation.pages.containers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.data.remote.dto.res.ProductPageResponse;
import com.example.btl_nhom1.app.domain.model.Product;
import com.example.btl_nhom1.app.domain.repository.ProductRepository;
import com.example.btl_nhom1.app.presentation.common.CustomBottomNavigationView;
import com.example.btl_nhom1.app.presentation.common.CustomCategoryDrawer;
import com.example.btl_nhom1.app.presentation.pages.details.ProductDetailsActivity;
import com.example.btl_nhom1.app.presentation.pages.home.HomePageActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContainerActivity extends AppCompatActivity implements CustomBottomNavigationView.OnBottomNavigationItemClickListener {
    private final int pageSize = 6;
    private CustomCategoryDrawer customCategoryDrawer;
    private CustomBottomNavigationView customBottomNav;
    private Context context;
    private LinearLayout btnFilter, btnSort;
    private FrameLayout fragmentContainer;
    private LinearLayout productContainer;
    private LinearLayout paginationContainer;
    private ProductRepository repository;
    private TextView tvHome, tvCategoryName;
    private ImageView imgBanner;
    private int categoryId;
    private String categoryName;
    private String bannerUrl;
    private int currentPage = 0;
    private int totalPages = 1;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_containers);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        context = this;

        // Ánh xạ view
        btnFilter = findViewById(R.id.btnFilter);
        btnSort = findViewById(R.id.btnSort);
        fragmentContainer = findViewById(R.id.fragment_container);
        productContainer = findViewById(R.id.productContainer);
        paginationContainer = findViewById(R.id.paginationContainer);

        tvHome = findViewById(R.id.tvHome);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        imgBanner = findViewById(R.id.imgBanner);

        repository = new ProductRepository(context);

        categoryId = getIntent().getIntExtra("categoryId", -1);
        categoryName = getIntent().getStringExtra("categoryName");
        bannerUrl = getIntent().getStringExtra("bannerUrl");

        Log.i("ContainerActivity", "===== START =====");
        Log.i("ContainerActivity", "Category ID: " + categoryId);
        Log.i("ContainerActivity", "Category Name: " + categoryName);
        Log.i("ContainerActivity", "Banner is null? " + (bannerUrl == null));
        Log.i("ContainerActivity", "Banner is empty? " + (bannerUrl != null && bannerUrl.isEmpty()));

        initViews();
        setupBreadcrumb();
        setupBanner();

        if (categoryId != -1) {
            loadProductsByCategory(categoryId, 0);
        } else {
            Log.e("ContainerActivity", "CATEGORY_ID = -1");
            Toast.makeText(this, "Lỗi: Không nhận được ID danh mục", Toast.LENGTH_LONG).show();
        }

        btnFilter.setOnClickListener(v -> openFragment(new FilterFragment()));
        btnSort.setOnClickListener(v -> openFragment(new SortFragment()));
    }

    private void initViews() {
        customCategoryDrawer = findViewById(R.id.customCategoryDrawer);
        customBottomNav = findViewById(R.id.customBottomNav);

        if (customBottomNav != null) {
            customBottomNav.setOnBottomNavigationItemClickListener(this);
        } else {
            Log.e("ContainerActivity", "CustomBottomNavigationView not found.");
        }
    }

    @Override
    public void onCategoryMenuClicked() {
        if (customCategoryDrawer != null) {
            customCategoryDrawer.openDrawer();
        }
    }

    private void setupBreadcrumb() {
        if (categoryName != null && !categoryName.isEmpty()) {
            tvCategoryName.setText(categoryName);
        }

        tvHome.setOnClickListener(v -> {
            Intent intent = new Intent(context, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setupBanner() {
        if (bannerUrl != null && !bannerUrl.isEmpty()) {
            int bannerResId = getResources().getIdentifier(
                    bannerUrl,
                    "drawable",
                    getPackageName()
            );

            if (bannerResId != 0) {
                imgBanner.setImageResource(bannerResId);
                Log.d("ContainerActivity", "Load banner: " + bannerUrl);
            } else {
                imgBanner.setImageResource(R.drawable.banner_disney);
                Log.w("ContainerActivity", "Không tìm thấy banner: " + bannerUrl);
            }
        } else {
            imgBanner.setImageResource(R.drawable.banner_disney);
            Log.d("ContainerActivity", "Dùng banner mặc định");
        }
    }

    private void loadProductsByCategory(int id, int pageNumber) {
        Log.i("ProductLoad", "Đang tải trang " + (pageNumber + 1) + "...");

        repository.getProductsByCategory(id, pageNumber, pageSize, new ProductRepository.ProductPageCallback() {
            @Override
            public void onSuccess(ProductPageResponse response) {
                allProducts = response.getContent();
                totalPages = response.getTotalPages();
                currentPage = pageNumber;

                Log.i("ProductLoad", "Tải thành công " + allProducts.size() + " sản phẩm");
                Log.i("ProductLoad", "Trang " + (pageNumber + 1) + "/" + totalPages);

                for (Product p : allProducts) {
                    Log.d("ProductLoad", "  • " + p.getName() + " - " + p.getPrice() + "₫");
                }

                runOnUiThread(() -> {
                    displayProducts(allProducts, productContainer);

                    createPaginationButtons();

                    Toast.makeText(context, "Trang " + (pageNumber + 1) + "/" + totalPages, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ProductLoad", "Lỗi tải sản phẩm: " + errorMessage);
                runOnUiThread(() ->
                        Toast.makeText(context, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    private void createPaginationButtons() {
        paginationContainer.removeAllViews();

        if (totalPages <= 1) {
            return;
        }

        Button btnPrev = createPaginationButton("‹");
        btnPrev.setEnabled(currentPage > 0);
        btnPrev.setAlpha(currentPage > 0 ? 1.0f : 0.5f);
        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                loadProductsByCategory(categoryId, currentPage - 1);
            }
        });
        paginationContainer.addView(btnPrev);

        addPageButton(0);

        if (currentPage > 1 && currentPage < totalPages - 1) {
            addDots();

            // Trang hiện tại
            addPageButton(currentPage);

            addDots();
        } else if (totalPages > 2) {
            // Hiển thị trang 2
            addPageButton(1);

            // Nếu có nhiều hơn 3 trang, thêm dấu ...
            if (totalPages > 3) {
                addDots();
            }
        }

        if (totalPages > 1) {
            addPageButton(totalPages - 1);
        }

        Button btnNext = createPaginationButton("›");
        btnNext.setEnabled(currentPage < totalPages - 1);
        btnNext.setAlpha(currentPage < totalPages - 1 ? 1.0f : 0.5f);
        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                loadProductsByCategory(categoryId, currentPage + 1);
            }
        });
        paginationContainer.addView(btnNext);
    }

    private void addPageButton(int pageIndex) {
        Button btnPage = createPaginationButton(String.valueOf(pageIndex + 1));

        // Highlight trang hiện tại
        if (pageIndex == currentPage) {
            btnPage.setBackgroundResource(R.drawable.pagination_active_bg);
            btnPage.setTextColor(Color.WHITE);
        } else {
            btnPage.setBackgroundResource(R.drawable.pagination_button_bg);
            btnPage.setTextColor(Color.parseColor("#666666"));
        }

        btnPage.setOnClickListener(v -> loadProductsByCategory(categoryId, pageIndex));
        paginationContainer.addView(btnPage);
    }

    private void addDots() {
        TextView dots = new TextView(context);
        dots.setText("...");
        dots.setTextSize(14);
        dots.setTextColor(Color.parseColor("#666666"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dpToPx(40)
        );
        params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        dots.setLayoutParams(params);
        dots.setGravity(Gravity.CENTER);

        paginationContainer.addView(dots);
    }

    private Button createPaginationButton(String text) {
        Button button = new Button(context);
        button.setText(text);
        button.setTextSize(14);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
        );
        params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        button.setLayoutParams(params);
        button.setBackgroundResource(R.drawable.pagination_button_bg);
        button.setTextColor(Color.parseColor("#666666"));
        button.setPadding(0, 0, 0, 0);

        return button;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private void displayProducts(List<Product> products, LinearLayout container) {
        container.removeAllViews();

        if (products == null || products.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Không có sản phẩm nào");
            emptyText.setTextSize(16);
            emptyText.setTextColor(Color.GRAY);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(0, dpToPx(40), 0, dpToPx(40));
            container.addView(emptyText);
            return;
        }

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
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.item_product, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        int margin = (int) (4 * getResources().getDisplayMetrics().density);
        params.setMargins(margin, margin, margin, margin);
        view.setLayoutParams(params);

        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        TextView tvProductName = view.findViewById(R.id.tvProductName);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        TextView tvSold = view.findViewById(R.id.tvSold);

        // Hiển thị tên
        String displayName = product.getDisplayName() != null ? product.getDisplayName() : product.getName();
        tvProductName.setText(displayName);

        // Format giá tiền
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String priceFormatted = formatter.format(product.getPrice()) + "₫";
        tvPrice.setText(priceFormatted);

        // Hiển thị số lượng bán
        if (product.getSoldQuantity() > 0) {
            tvSold.setText(product.getSoldQuantity() + " đã bán");
            tvSold.setVisibility(View.VISIBLE);
        } else {
            tvSold.setVisibility(View.GONE);
        }

        // Load hình ảnh từ drawable
        String imageName = product.getPrimaryImageUrl();
        int imageResId = 0;

        if (imageName != null && !imageName.isEmpty()) {
            imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        }

        if (imageResId != 0) {
            imgProduct.setImageResource(imageResId);
        } else {
            imgProduct.setImageResource(R.drawable.nullimage);
        }

        // Click vào sản phẩm
        view.setOnClickListener(v -> {
            Intent intent = new Intent(ContainerActivity.this, ProductDetailsActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            intent.putExtra("PRODUCT_NAME", product.getName());
            startActivity(intent);
        });

        return view;
    }

    private void openFragment(Fragment fragment) {
        fragmentContainer.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                fragmentContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportFragmentManager().removeOnBackStackChangedListener(() -> {
        });
    }
}