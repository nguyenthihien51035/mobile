package com.example.btl_nhom1.app.domain.repository;

import android.util.Log;

import com.example.btl_nhom1.app.domain.model.Category;
import com.example.btl_nhom1.app.dto.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryRepository {
    private static final String TAG = "CategoryRepository";
    private static final String BASE_URL = "http://10.12.118.203/api/";
    private static final String API_LATEST = BASE_URL + "getCategoryTree.php";

    private final OkHttpClient client;
    private final Gson gson;

    public CategoryRepository() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    // Lấy danh sách categories từ API
    public void getCategories(CategoryCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(API_LATEST)
                        .get()
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "Response: " + jsonResponse);

                    // Sử dụng ApiResponse generic với TypeToken
                    Type responseType = new TypeToken<ApiResponse<List<Category>>>() {
                    }.getType();
                    ApiResponse<List<Category>> apiResponse = gson.fromJson(jsonResponse, responseType);

                    if (apiResponse != null && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError("Dữ liệu trả về không hợp lệ");
                    }
                } else {
                    callback.onError("Lỗi kết nối: " + response.code());
                }

                response.close();

            } catch (IOException e) {
                Log.e(TAG, "Error fetching categories", e);
                callback.onError("Lỗi kết nối: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Error parsing data", e);
                callback.onError("Lỗi xử lý dữ liệu: " + e.getMessage());
            }
        }).start();
    }

    // Interface callback để xử lý kết quả bất đồng bộ
    public interface CategoryCallback {
        void onSuccess(List<Category> categories);

        void onError(String errorMessage);
    }
}