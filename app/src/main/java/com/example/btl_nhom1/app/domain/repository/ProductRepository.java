package com.example.btl_nhom1.app.domain.repository;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_nhom1.app.data.remote.dto.ApiResponse;
import com.example.btl_nhom1.app.domain.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ProductRepository {
    private static final String BASE_URL = "http://192.168.100.205/api/";
    private static final String API_LATEST = BASE_URL + "getlatest.php?action=latest";
    private static final String API_TOP_SELLING = BASE_URL + "getTopSellingProducts.php?action=top-selling";
    private static final String API_PRODUCT_DETAILS = BASE_URL + "getProductDetails.php?id=";

    private final RequestQueue requestQueue;
    private final Context context;
    private final Gson gson;

    public ProductRepository(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.gson = new Gson();
    }

    public void fetchLatestProducts(ProductCallback callback) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_LATEST,
                response -> {
                    try {
                        Type responseType = new TypeToken<ApiResponse<List<Product>>>() {
                        }.getType();
                        ApiResponse<List<Product>> apiResponse = gson.fromJson(response, responseType);

                        if (apiResponse != null && apiResponse.getData() != null) {
                            callback.onSuccess(apiResponse.getData());
                        } else {
                            callback.onError("Không có dữ liệu");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError("Lỗi parse dữ liệu: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.onError("Lỗi kết nối API: " + error.getMessage());
                }
        );

        requestQueue.add(stringRequest);
    }

    public void fetchTopSellingProducts(ProductCallback callback) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_TOP_SELLING,
                response -> {
                    try {
                        Type responseType = new TypeToken<ApiResponse<List<Product>>>() {
                        }.getType();
                        ApiResponse<List<Product>> apiResponse = gson.fromJson(response, responseType);

                        if (apiResponse != null && apiResponse.getData() != null) {
                            callback.onSuccess(apiResponse.getData());
                        } else {
                            callback.onError("Không có dữ liệu");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("ProductRepository", "Lỗi parse dữ liệu: " + e.getMessage(), e);
                        callback.onError("Lỗi parse dữ liệu: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.onError("Lỗi kết nối API: " + error.getMessage());
                }
        );

        requestQueue.add(stringRequest);
    }

    public void fetchProductDetails(int productId, ProductDetailsCallback callback) {
        String url = API_PRODUCT_DETAILS + productId;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        Type responseType = new TypeToken<ApiResponse<List<Product>>>() {
                        }.getType();
                        ApiResponse<List<Product>> apiResponse = gson.fromJson(response, responseType);

                        if (apiResponse != null && apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                            callback.onSuccess(apiResponse.getData().get(0));
                        } else {
                            callback.onError("Không tìm thấy sản phẩm");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("ProductRepository", "Lỗi parse chi tiết sản phẩm: " + e.getMessage(), e);
                        callback.onError("Lỗi parse dữ liệu: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    Log.e("ProductRepository", "Lỗi kết nối API chi tiết: " + error.getMessage(), error);
                    callback.onError("Lỗi kết nối API: " + error.getMessage());
                }
        );

        requestQueue.add(stringRequest);
    }

    public void cancelAllRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll(request -> true);
        }
    }

    public interface ProductDetailsCallback {
        void onSuccess(Product product);

        void onError(String errorMessage);
    }

    /**
     * Interface callback để trả kết quả
     */
    public interface ProductCallback {
        void onSuccess(List<Product> products);

        void onError(String errorMessage);
    }
}