package com.example.btl_nhom1.app.presentation.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Product;
import com.example.btl_nhom1.app.domain.model.ProductImage;
import com.example.btl_nhom1.app.domain.repository.ProductRepository;
import com.example.btl_nhom1.app.presentation.adapter.ProductImageAdapter;

import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    Context context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        context = this;
        productRepository = new ProductRepository(context);

        Intent intent = getIntent();
        productId = intent.getIntExtra("PRODUCT_ID", -1);

        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initViews();

        // Setup listeners
        setupListeners();

        // Load product details
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
                Toast.makeText(this, "Mua ngay: " + currentProduct.getName(), Toast.LENGTH_SHORT).show();
                // TODO: Implement buy now logic
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            if (currentProduct != null) {
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                // TODO: Implement add to cart logic
            }
        });

        btnBuyFree.setOnClickListener(v -> {
            // TODO: Implement call function
            Toast.makeText(this, "Gọi điện thoại", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProductDetails() {
        // Show loading indicator
        showLoading();

        productRepository.fetchProductDetails(productId, new ProductRepository.ProductDetailsCallback() {
            @Override
            public void onSuccess(Product product) {
                hideLoading();
                currentProduct = product;
                updateUI(product);
            }

            @Override
            public void onError(String errorMessage) {
                hideLoading();
                Toast.makeText(context, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("ProductDetails", "Error: " + errorMessage);
            }
        });
    }

    private void updateUI(Product product) {
        // Update product name
        tvProductName.setText(product.getName());

        // Update product code (SKU)
        tvProductCode.setText("Mã: " + product.getSku());

        // Update rating (if you have rating data)
        tvRating.setText("(0)"); // Default to 0 if no rating

        // Update price
        String formattedPrice = String.format("%,.0f ₫", product.getPrice());
        tvPrice.setText(formattedPrice);

        // Calculate installment (price / 12 months)
        double monthlyPayment = product.getPrice() / 12;
        String formattedInstallment = String.format("Chỉ cần trả %,.0f ₫/tháng", monthlyPayment);
        tvInstallment.setText(formattedInstallment);

        // Update size (show first size if available)
        if (product.getSizes() != null && !product.getSizes().isEmpty()) {
            tvSize.setText(product.getSizes().get(0).getSize());
            tvSize.setVisibility(View.VISIBLE);
        } else {
            tvSize.setVisibility(View.GONE);
        }

        // Setup image slider
        setupImageSlider(product.getImages());
    }

    private void setupImageSlider(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            tvImageIndicator.setText("0/0");
            return;
        }

        ProductImageAdapter adapter = new ProductImageAdapter(images);
        viewPager.setAdapter(adapter);

        tvImageIndicator.setText("1/" + images.size());

        // Add page change listener
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvImageIndicator.setText((position + 1) + "/" + images.size());
            }
        });
    }

    private void showLoading() {
        // TODO: Show loading dialog or progress bar
        btnBuyNow.setEnabled(false);
        btnAddToCart.setEnabled(false);
    }

    private void hideLoading() {
        // TODO: Hide loading dialog or progress bar
        btnBuyNow.setEnabled(true);
        btnAddToCart.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productRepository != null) {
            productRepository.cancelAllRequests();
        }
    }
}
