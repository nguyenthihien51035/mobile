package com.example.btl_nhom1.app.domain.repository;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_nhom1.app.data.remote.dto.ApiResponse;
import com.example.btl_nhom1.app.data.remote.dto.res.ProductPageResponse;
import com.example.btl_nhom1.app.domain.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ProductRepository {
    private static final String BASE_URL = "http://192.168.100.253/api/";
    private static final String API_LATEST = BASE_URL + "getlatest.php?action=latest";
    private static final String API_TOP_SELLING = BASE_URL + "getTopSellingProducts.php?action=top-selling";
    private static final String API_PRODUCT_DETAILS = BASE_URL + "getProductDetails.php?id=";
    private static final String API_FILTER = BASE_URL + "getFilteredProducts.php";
    private static final String API_GOLD_TYPES = BASE_URL + "getAllGoldTypes.php?action=goldTypes";
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

    public void fetchGoldTypes(GoldTypesCallback callback) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                API_GOLD_TYPES,
                response -> {
                    try {
                        Log.d("ProductRepository", "GoldTypes Response: " + response);

                        Type responseType = new TypeToken<ApiResponse<List<String>>>() {
                        }.getType();
                        ApiResponse<List<String>> apiResponse = gson.fromJson(response, responseType);

                        if (apiResponse != null && apiResponse.getData() != null) {
                            callback.onSuccess(apiResponse.getData());
                        } else {
                            callback.onError("Không có dữ liệu loại vàng");
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

    public void getFilteredProducts(
            int categoryId,
            int pageNumber,
            int pageSize,
            String goldType,
            Integer fromPrice,
            Integer toPrice,
            String sortBy,
            String sortDirection,
            ProductPageCallback callback) {
        StringBuilder urlBuilder = new StringBuilder(API_FILTER);
        urlBuilder.append("?action=filterByCategory");
        urlBuilder.append("&id=").append(categoryId);
        urlBuilder.append("&pageSize=").append(pageSize);
        urlBuilder.append("&pageNumber=").append(pageNumber);

        // Filter params
        if (goldType != null && !goldType.isEmpty()) {
            urlBuilder.append("&goldType=").append(goldType);
        }
        if (fromPrice != null) {
            urlBuilder.append("&fromPrice=").append(fromPrice);
        }
        if (toPrice != null) {
            urlBuilder.append("&toPrice=").append(toPrice);
        }

        // Sort params
        if (sortBy != null && !sortBy.isEmpty()) {
            urlBuilder.append("&sortBy=").append(sortBy);
        }
        if (sortDirection != null && !sortDirection.isEmpty()) {
            urlBuilder.append("&sortDirection=").append(sortDirection);
        }

        String url = urlBuilder.toString();
        Log.i("ProductRepository", "📡 Filtered API: " + url);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        Log.d("ProductRepository", "Response: " + response);

                        Type responseType = new TypeToken<ApiResponse<ProductPageResponse>>() {
                        }.getType();
                        ApiResponse<ProductPageResponse> apiResponse = gson.fromJson(response, responseType);

                        if (apiResponse != null && apiResponse.getData() != null) {
                            callback.onSuccess(apiResponse.getData());
                        } else {
                            callback.onError("Không có dữ liệu");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError("Lỗi parse: " + e.getMessage());
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.onError("Lỗi API: " + error.getMessage());
                }
        );

        requestQueue.add(stringRequest);
    }


    public void cancelAllRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll(request -> true);
        }
    }

    /**
     * Interface callback để trả kết quả
     */
    public interface ProductCallback {
        void onSuccess(List<Product> products);

        void onError(String errorMessage);
    }

    public interface ProductDetailsCallback {
        void onSuccess(Product product);

        void onError(String errorMessage);
    }

    public interface ProductPageCallback {
        void onSuccess(ProductPageResponse response);

        void onError(String errorMessage);
    }

    public interface GoldTypesCallback {
        void onSuccess(List<String> goldTypes);

        void onError(String errorMessage);
    }
}