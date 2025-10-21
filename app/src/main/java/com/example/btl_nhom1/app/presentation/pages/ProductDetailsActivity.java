package com.example.btl_nhom1.app.presentation.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Product;
import com.example.btl_nhom1.app.domain.model.ProductImage;
import com.example.btl_nhom1.app.domain.repository.ProductRepository;
import com.example.btl_nhom1.app.presentation.adapter.ProductImageAdapter;
import com.example.btl_nhom1.app.presentation.common.CustomBottomNavigationView;
import com.example.btl_nhom1.app.presentation.common.CustomCategoryDrawer;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity implements CustomBottomNavigationView.OnBottomNavigationItemClickListener {
    private static final String TAG = "ProductDetails";

    private Context context;
    private CustomCategoryDrawer customCategoryDrawer;
    private CustomBottomNavigationView customBottomNav;
    private ProductRepository productRepository;
    private ViewPager2 viewPager;
    private TextView tvImageIndicator;
    private TextView tvProductName;
    private TextView tvProductCode;
    private TextView tvRating;
    private TextView tvPrice;
    private TextView tvInstallment;
    private TextView tvSize;
    private Button btnBuyNow;
    private Button btnAddToCart;
    private Button btnBuyFree;

    private Product currentProduct;
    private int productId;

    // Keep reference to callback to unregister later
    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        context = this;
        productRepository = new ProductRepository(context);

        Intent intent = getIntent();
        productId = intent.getIntExtra("PRODUCT_ID", -1);
        Log.d(TAG, "onCreate productId=" + productId);

        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        loadProductDetails();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tvImageIndicator = findViewById(R.id.tvImageIndicator);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductCode = findViewById(R.id.tvProductCode);
        tvRating = findViewById(R.id.tvRating);
        tvPrice = findViewById(R.id.tvPrice);
        tvInstallment = findViewById(R.id.tvInstallment);
        tvSize = findViewById(R.id.tvSize);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyFree = findViewById(R.id.btnBuyFree);
    }

    private void setupListeners() {
        btnBuyNow.setOnClickListener(v -> {
            if (currentProduct != null) {
                // TODO: mở activity/flow mua hàng; hiện tại show Toast
                Toast.makeText(context, "Mua ngay: " + safeString(currentProduct.getName()), Toast.LENGTH_SHORT).show();

                // ví dụ mở activity Checkout (nếu có)
                // Intent checkout = new Intent(context, CheckoutActivity.class);
                // checkout.putExtra("PRODUCT_ID", currentProduct.getId());
                // startActivity(checkout);
            } else {
                Toast.makeText(context, "Sản phẩm chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null) {
                addToCart(currentProduct);
                Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Sản phẩm chưa sẵn sàng", Toast.LENGTH_SHORT).show();
            }
        });

//        btnBuyFree.setOnClickListener(v -> {
//            // Gọi điện: mở dialer với số (lấy từ product nếu có), nếu không có thì báo cho user
//            String phone = null;
//            if (currentProduct != null && currentProduct.getPhone() != null && !currentProduct.getPhone().isEmpty()) {
//                phone = currentProduct.getPhone();
//            }
//
//            if (phone == null || phone.isEmpty()) {
//                // Thay bằng số mặc định cửa hàng nếu bạn muốn
//                Toast.makeText(context, "Số liên hệ chưa có", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            Intent intent = new Intent(Intent.ACTION_DIAL);
//            intent.setData(Uri.parse("tel:" + phone));
//            startActivity(intent);
//        });
    }

    private void loadProductDetails() {
        showLoading();
        productRepository.fetchProductDetails(productId, new ProductRepository.ProductDetailsCallback() {
            @Override
            public void onSuccess(final Product product) {
                Log.d(TAG, "fetch onSuccess product=" + (product == null ? "null" : product.getName()));
                if (product == null) {
                    runOnUiThread(() -> {
                        hideLoading();
                        Toast.makeText(context, "Sản phẩm rỗng", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }

                runOnUiThread(() -> {
                    hideLoading();
                    currentProduct = product;
                    try {
                        updateUI(product);
                    } catch (Exception e) {
                        Log.e(TAG, "updateUI error", e);
                        Toast.makeText(context, "Lỗi cập nhật giao diện", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(final String errorMessage) {
                Log.e(TAG, "fetch onError: " + errorMessage);
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(context, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateUI(Product product) {
        // Name
        tvProductName.setText(safeString(product.getName()));

        // SKU / Code
        tvProductCode.setText("Mã: " + safeString(product.getSku()));

        // Rating (nếu product có)
//        if (product.getRating() != null) {
//            tvRating.setText("(" + product.getRating() + ")");
//        } else {
//            tvRating.setText("(0)");
//        }

        // Price - xử lý an toàn với nhiều kiểu trả về
        double priceValue = safeDouble(product.getPrice());
        tvPrice.setText(formatCurrency(priceValue));

        // Installment (12 tháng)
        double monthly = priceValue / 12.0;
        tvInstallment.setText("Chỉ cần trả " + formatCurrency(monthly) + "/tháng");

        // Size
        if (product.getSizes() != null && !product.getSizes().isEmpty()) {
            String firstSize = product.getSizes().get(0).getSize();
            tvSize.setText(safeString(firstSize));
            tvSize.setVisibility(View.VISIBLE);
        } else {
            tvSize.setVisibility(View.GONE);
        }

        // Images
        setupImageSlider(product.getImages());
    }

    private void setupImageSlider(final List<ProductImage> images) {
        runOnUiThread(() -> {
            if (images == null || images.isEmpty()) {
                tvImageIndicator.setText("0/0");
                // You may set a placeholder image to ViewPager's adapter if you want
                return;
            }

            ProductImageAdapter adapter = new ProductImageAdapter(images);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(1);
            tvImageIndicator.setText("1/" + images.size());

            // Remove old callback if set
            if (pageChangeCallback != null) {
                try {
                    viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
                } catch (Exception ignored) {
                }
            }

            pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    tvImageIndicator.setText((position + 1) + "/" + images.size());
                }
            };
            viewPager.registerOnPageChangeCallback(pageChangeCallback);
        });
    }

    private void addToCart(Product product) {
        // Simple local add-to-cart: lưu vào SharedPreferences dưới dạng id list hoặc JSON
        // Đây là demo đơn giản; bạn có thể gọi repository/cart manager thực sự
        try {
            // TODO: replace bằng logic lưu cart thực tế
            Log.d(TAG, "addToCart: " + product.getId() + " - " + product.getName());
        } catch (Exception e) {
            Log.e(TAG, "addToCart error", e);
            Toast.makeText(context, "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private String safeString(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private double safeDouble(Object value) {
        if (value == null) return 0d;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value).replaceAll("[^0-9\\.-]", ""));
        } catch (Exception e) {
            Log.w(TAG, "safeDouble parse error for value=" + value, e);
            return 0d;
        }
    }

    private String formatCurrency(double amount) {
        try {
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            nf.setMaximumFractionDigits(0);
            return nf.format(amount) + " ₫";
        } catch (Exception e) {
            return String.format("%,.0f ₫", amount);
        }
    }

    private void showLoading() {
        runOnUiThread(() -> {
            if (btnBuyNow != null) btnBuyNow.setEnabled(false);
            if (btnAddToCart != null) btnAddToCart.setEnabled(false);
            if (btnBuyFree != null) btnBuyFree.setEnabled(false);
            // TODO: show ProgressBar nếu bạn có
        });
    }

    private void hideLoading() {
        runOnUiThread(() -> {
            if (btnBuyNow != null) btnBuyNow.setEnabled(true);
            if (btnAddToCart != null) btnAddToCart.setEnabled(true);
            if (btnBuyFree != null) btnBuyFree.setEnabled(true);
            // TODO: hide ProgressBar nếu có
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (pageChangeCallback != null && viewPager != null) {
                viewPager.unregisterOnPageChangeCallback(pageChangeCallback);
            }
        } catch (Exception ignored) {
        }

        if (productRepository != null) {
            productRepository.cancelAllRequests();
        }
    }

    @Override
    public void onCategoryMenuClicked() {
        if (customCategoryDrawer != null) {
            customCategoryDrawer.openDrawer();
        }
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

}
