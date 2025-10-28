package com.example.btl_nhom1.app.domain.repository;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_nhom1.app.domain.model.Account;
import com.example.btl_nhom1.app.dto.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AccountRepository {
    private static final String TAG = "AccountRepository";
    private static final String BASE_URL = "http://192.168.100.205/api/";
    private static final String API_LOGIN = BASE_URL + "postLogin.php";

    private final RequestQueue requestQueue;
    private final Gson gson;

    public AccountRepository(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.gson = new Gson();
    }

    public void login(String account, String password, LoginCallback callback) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_LOGIN,
                response -> {
                    try {
                        Log.d(TAG, "Response: " + response);

                        Type type = new TypeToken<ApiResponse<Account>>() {
                        }.getType();
                        ApiResponse<Account> apiResponse = gson.fromJson(response, type);

                        if (apiResponse != null && apiResponse.getData() != null) {
                            callback.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                        } else {
                            String msg = apiResponse != null ? apiResponse.getMessage() : "Lỗi không xác định";
                            callback.onError(msg);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parse dữ liệu: " + e.getMessage(), e);
                        callback.onError("Lỗi xử lý dữ liệu");
                    }
                },
                error -> {
                    String errorMsg = "Lỗi kết nối API";
                    if (error.networkResponse != null)
                        errorMsg += " (Code: " + error.networkResponse.statusCode + ")";
                    Log.e(TAG, errorMsg, error);
                    callback.onError(errorMsg);
                }
        ) {
            @Override
            public byte[] getBody() {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("account", account);
                    params.put("password", password);

                    String jsonBody = new JSONObject(params).toString();
                    return jsonBody.getBytes(StandardCharsets.UTF_8);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi tạo body JSON", e);
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        requestQueue.add(stringRequest);
    }

    public void cancelAllRequests() {
        if (requestQueue != null) requestQueue.cancelAll(request -> true);
    }

    public interface LoginCallback {
        void onSuccess(Account account, String message);

        void onError(String errorMessage);
    }
}