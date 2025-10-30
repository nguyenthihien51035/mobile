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

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountRepository {
    private static final String TAG = "AccountRepository";
    private static final String BASE_URL = "http://10.12.118.203/api/";
    private static final String API_LOGIN = BASE_URL + "postLogin.php";
    private static final String API_GET_ACCOUNT = BASE_URL + "getAccountById.php";
    private static final String API_CHANGE_PASSWORD = BASE_URL + "putChangePassword.php";

    private final RequestQueue requestQueue;
    private final Gson gson;
    private final OkHttpClient okHttpClient;

    public AccountRepository(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.gson = new Gson();

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
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

    public void getAccountById(int userId, AccountCallback callback) {
        String url = API_GET_ACCOUNT + "?action=getById&id=" + userId;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        Log.d(TAG, "GetAccount Response: " + response);

                        Type type = new TypeToken<ApiResponse<Account>>() {
                        }.getType();
                        ApiResponse<Account> apiResponse = gson.fromJson(response, type);

                        if (apiResponse != null && apiResponse.getData() != null) {
                            callback.onSuccess(apiResponse.getData());
                        } else {
                            String msg = apiResponse != null ? apiResponse.getMessage() : "Không có dữ liệu";
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
        );

        requestQueue.add(stringRequest);
    }

    public void changePassword(int userId, String currentPassword, String newPassword, ChangePasswordCallback callback) {
        String url = API_CHANGE_PASSWORD + "?id=" + userId;

        try {
            // Create JSON body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("currentPassword", currentPassword);
            jsonBody.put("newPassword", newPassword);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            // Build request
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .put(body)
                    .build();

            // Execute async
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "ChangePassword network error: " + e.getMessage(), e);
                    callback.onError("Lỗi kết nối: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d(TAG, "ChangePassword Response Code: " + response.code());
                    Log.d(TAG, "ChangePassword Response Body: " + responseBody);

                    if (response.isSuccessful()) {
                        try {
                            Type type = new TypeToken<ApiResponse<Object>>() {
                            }.getType();
                            ApiResponse<Object> apiResponse = gson.fromJson(responseBody, type);

                            if (apiResponse != null && apiResponse.getErrorMessage() == null) {
                                callback.onSuccess(apiResponse.getMessage());
                            } else {
                                String msg = apiResponse != null ? apiResponse.getMessage() : "Lỗi không xác định";
                                callback.onError(msg);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Parse error: " + e.getMessage(), e);
                            callback.onError("Lỗi xử lý dữ liệu");
                        }
                    } else {
                        // Parse error response
                        try {
                            Type type = new TypeToken<ApiResponse<Object>>() {
                            }.getType();
                            ApiResponse<Object> errorResponse = gson.fromJson(responseBody, type);

                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                callback.onError(errorResponse.getMessage());
                            } else {
                                callback.onError("Lỗi HTTP: " + response.code());
                            }
                        } catch (Exception e) {
                            callback.onError("Lỗi HTTP: " + response.code());
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating request: " + e.getMessage(), e);
            callback.onError("Lỗi tạo request: " + e.getMessage());
        }
    }

    public void cancelAllRequests() {
        if (requestQueue != null) requestQueue.cancelAll(request -> true);
    }

    public interface LoginCallback {
        void onSuccess(Account account, String message);

        void onError(String errorMessage);
    }

    // Callback mới cho getAccountById
    public interface AccountCallback {
        void onSuccess(Account account);

        void onError(String errorMessage);
    }

    public interface ChangePasswordCallback {
        void onSuccess(String message);

        void onError(String errorMessage);
    }
}
