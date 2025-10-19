package com.example.btl_nhom1.app.domain.repository;

import android.content.Context;

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
    private static final String BASE_URL = "http://192.168.1.78/api/";
    private static final String API_LATEST = BASE_URL + "getlatest.php?action=latest";

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
}