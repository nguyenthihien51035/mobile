package com.example.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "http://192.168.100.253/api/getlatest.php?action=latest";
    private LinearLayout productContainer;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productContainer = findViewById(R.id.productContainer);
        requestQueue = Volley.newRequestQueue(this);

        // Gọi API để lấy danh sách sản phẩm
        fetchProducts();
    }

    private void fetchProducts() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
                response -> {
                    try {
                        // Parse JSON response
                        Gson gson = new Gson();
                        ApiResponse productResponse = gson.fromJson(response, ApiResponse.class);

                        if (productResponse != null && productResponse.getData() != null) {
                            displayProducts(productResponse.getData());
                        } else {
                            Toast.makeText(MainActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Lỗi parse dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(MainActivity.this, "Lỗi kết nối API: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(stringRequest);
    }

    private void displayProducts(List<Product> products) {
        // Xóa tất cả view cũ
        productContainer.removeAllViews();

        // Tạo các hàng, mỗi hàng chứa 2 sản phẩm
        for (int i = 0; i < products.size(); i += 2) {
            LinearLayout row = createProductRow();

            // Thêm sản phẩm 1
            View productView1 = createProductView(products.get(i));
            row.addView(productView1);

            // Thêm sản phẩm 2 nếu còn
            if (i + 1 < products.size()) {
                View productView2 = createProductView(products.get(i + 1));
                row.addView(productView2);
            } else {
                // Thêm view trống để cân bằng layout
                View emptyView = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                emptyView.setLayoutParams(params);
                row.addView(emptyView);
            }

            productContainer.addView(row);
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
        // Inflate layout từ XML - PHẢI DÙNG item_product
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
        String imageName = product.getPrimaryImageUrl(); // Tên ảnh không có đuôi
        int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());

        if (imageResId != 0) {
            // Nếu tìm thấy ảnh trong drawable
            imgProduct.setImageResource(imageResId);
        } else {
            // Nếu không tìm thấy, dùng ảnh placeholder
            imgProduct.setImageResource(R.drawable.nullimage);
        }

        // Set click listener
        view.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
            // TODO: Mở trang chi tiết sản phẩm
        });

        return view;
    }
}