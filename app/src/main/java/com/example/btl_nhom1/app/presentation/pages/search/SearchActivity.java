package com.example.btl_nhom1.app.presentation.pages.search;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Product;
import com.example.btl_nhom1.app.domain.repository.ProductRepository;
import com.example.btl_nhom1.app.presentation.adapter.SearchAdapter;
import com.example.btl_nhom1.app.presentation.pages.containers.ContainerActivity;
import com.example.btl_nhom1.app.presentation.pages.details.ProductDetailsActivity;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private int firstProductCategoryId = -1;
    private static final long SEARCH_DELAY = 500; // 0.5 giây debounce

    private EditText searchEditText;
    private ImageView backButton, clearIcon;
    private RecyclerView searchRecyclerView;
    private ProgressBar progressBar;
    private TextView tvNoResults;
    private LinearLayout emptyStateLayout;

    private SearchAdapter adapter;
    private ProductRepository repository;
    private Handler searchHandler;
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_search_results);

        initViews();
        setupRecyclerView();
        setupListeners();

        repository = new ProductRepository(this);
        searchHandler = new Handler(Looper.getMainLooper());

        // Focus vào ô tìm kiếm và hiện bàn phím
        searchEditText.requestFocus();
    }

    private void initViews() {
        searchEditText = findViewById(R.id.searchEditText);
        backButton = findViewById(R.id.backButton);
        clearIcon = findViewById(R.id.clearIcon);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        progressBar = findViewById(R.id.progressBarSearch);
        tvNoResults = findViewById(R.id.tvNoResults);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }

    private void setupRecyclerView() {
        adapter = new SearchAdapter(this, product -> {
            // Click vào sản phẩm -> mở chi tiết
            Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            intent.putExtra("PRODUCT_NAME", product.getName());
            startActivity(intent);
        });

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        // Nút back
        backButton.setOnClickListener(v -> finish());

        // Nút xóa text
        clearIcon.setOnClickListener(v -> {
            searchEditText.setText("");
            clearIcon.setVisibility(View.GONE);
        });

        // Text watcher - tìm kiếm theo thời gian thực
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hiển thị/ẩn nút xóa
                clearIcon.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                // Hủy search trước đó
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Nếu rỗng, hiển thị empty state
                if (s.toString().trim().isEmpty()) {
                    showEmptyState();
                    return;
                }

                // Tạo runnable mới với debounce
                searchRunnable = () -> performSearch(s.toString().trim());
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String keyword = searchEditText.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    // Chuyển sang FilterActivity với keyword
                    navigateToFilter(keyword);
                }
                return true;
            }
            return false;
        });
    }

    //    private void navigateToFilter(String keyword) {
//        // Ẩn bàn phím
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
//
//        // Chuyển sang ContainerActivity với keyword
//        Intent intent = new Intent(SearchActivity.this, ContainerActivity.class);
//        intent.putExtra("search_keyword", keyword);
//        intent.putExtra("categoryId", -1); // -1 = tìm kiếm tất cả danh mục
//        intent.putExtra("categoryName", "Kết quả tìm kiếm: " + keyword);
//        intent.putExtra("bannerUrl", ""); // Không cần banner
//        startActivity(intent);
//
//        // Đóng SearchActivity
//        finish();
//    }
    private void navigateToFilter(String keyword) {
        // Ẩn bàn phím
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

        // Sử dụng categoryId đã lưu (mặc định -1 nếu chưa tìm)
        int categoryId = firstProductCategoryId != -1 ? firstProductCategoryId : -1;

        Intent intent = new Intent(SearchActivity.this, ContainerActivity.class);
        intent.putExtra("search_keyword", keyword);
        intent.putExtra("categoryId", categoryId); // ← DÙNG CATEGORY_ID ĐÃ LƯU
        intent.putExtra("categoryName", "Kết quả tìm kiếm: " + keyword);
        intent.putExtra("bannerUrl", "");

        Log.d(TAG, "Navigate with categoryId: " + categoryId + ", keyword: " + keyword);

        startActivity(intent);
        finish();
    }

    private void performSearch(String keyword) {
        Log.d(TAG, "Searching for: " + keyword);

        showLoading();

        repository.searchProducts(keyword, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                hideLoading();

                if (products == null || products.isEmpty()) {
                    showNoResults();
                    firstProductCategoryId = -1;
                } else {
                    // ← LẤY CATEGORY_ID CỦA SẢN PHẨM ĐẦU TIÊN
                    firstProductCategoryId = products.get(0).getCategoryId();
                    Log.d(TAG, "First product category ID: " + firstProductCategoryId);
                    showResults(products);
                }
            }

            @Override
            public void onError(String errorMessage) {
                hideLoading();
                Log.e(TAG, "Search error: " + errorMessage);
                showNoResults();
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        searchRecyclerView.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void showResults(List<Product> products) {
        adapter.setProducts(products);
        searchRecyclerView.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void showNoResults() {
        searchRecyclerView.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        searchRecyclerView.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        if (repository != null) {
            repository.cancelAllRequests();
        }
    }
}
