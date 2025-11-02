package com.example.btl_nhom1.app.domain.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_nhom1.app.domain.model.Account;
import com.example.btl_nhom1.app.dto.ApiResponse;
import com.example.btl_nhom1.app.dto.form.RegisterForm;
import com.example.btl_nhom1.app.module.VolleyMultipartRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
    private static final String BASE_URL = "http://192.168.100.253/api/";
    private static final String API_LOGIN = BASE_URL + "postLogin.php";
    private static final String API_GET_ACCOUNT = BASE_URL + "getAccountById.php";
    private static final String API_CHANGE_PASSWORD = BASE_URL + "putChangePassword.php";
    private static final String API_REGISTER = BASE_URL + "postRegister.php";
    private final RequestQueue requestQueue;
    private final Gson gson;
    private final Context context;
    private final OkHttpClient okHttpClient;

    public AccountRepository(Context context) {
        this.context = context.getApplicationContext();
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

    public void register(RegisterForm form, File avatarFile, RegisterCallback callback) {
        Log.d(TAG, "===== REGISTER API CALL =====");
        Log.d(TAG, "Username: " + form.getUserName());
        Log.d(TAG, "Email: " + form.getEmail());
        Log.d(TAG, "Avatar file: " + (avatarFile != null ? avatarFile.getName() : "null"));

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                API_REGISTER,
                response -> {
                    try {
                        String responseBody = new String(response.data, StandardCharsets.UTF_8);
                        Log.d(TAG, "===== RESPONSE RECEIVED =====");
                        Log.d(TAG, "Response Code: " + response.statusCode);
                        Log.d(TAG, "Response Length: " + responseBody.length() + " chars");
                        Log.d(TAG, "Response Body RAW: " + responseBody);
                        Log.d(TAG, "First 100 chars: " + responseBody.substring(0, Math.min(100, responseBody.length())));

                        if (response.statusCode == 201 || response.statusCode == 200) {
                            // Trim whitespace và check JSON hợp lệ
                            responseBody = responseBody.trim();

                            if (!responseBody.startsWith("{")) {
                                Log.e(TAG, "esponse is not JSON! First char: " + responseBody.charAt(0));
                                Log.e(TAG, "Full response: " + responseBody);
                                callback.onError("Server trả về dữ liệu không hợp lệ");
                                return;
                            }

                            // Parse response thành ApiResponse<Account>
                            Type type = new TypeToken<ApiResponse<Account>>() {
                            }.getType();
                            ApiResponse<Account> apiResponse = gson.fromJson(responseBody, type);

                            if (apiResponse != null && apiResponse.getData() != null) {
                                Log.d(TAG, "Registration successful");
                                callback.onSuccess(apiResponse.getData(), apiResponse.getMessage());
                            } else {
                                String msg = apiResponse != null ? apiResponse.getMessage() : "Đăng ký thất bại";
                                Log.w(TAG, "Registration failed: " + msg);
                                callback.onError(msg);
                            }
                        } else {
                            try {
                                JSONObject jsonError = new JSONObject(responseBody);
                                String errorMsg = jsonError.optString("message", "Lỗi không xác định");
                                String errorDetail = jsonError.optString("errorMessage", "");

                                Log.e(TAG, "Error " + response.statusCode + ": " + errorMsg +
                                        (errorDetail.isEmpty() ? "" : " (" + errorDetail + ")"));
                                callback.onError(errorMsg);
                            } catch (Exception e) {
                                Log.e(TAG, "Cannot parse error response", e);
                                callback.onError("Lỗi HTTP: " + response.statusCode);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing response: " + e.getMessage(), e);
                        callback.onError("Lỗi xử lý dữ liệu");
                    }
                },
                error -> {
                    Log.e(TAG, "Network error: " + error.toString());

                    String errorMsg = "Lỗi kết nối server";
                    if (error.networkResponse != null) {
                        try {
                            String errorBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            Log.e(TAG, "Error response: " + errorBody);

                            JSONObject jsonError = new JSONObject(errorBody);
                            errorMsg = jsonError.optString("message", errorMsg);
                        } catch (Exception e) {
                            Log.e(TAG, "Cannot parse error body", e);
                        }
                    }

                    callback.onError(errorMsg);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // Map từ RegisterForm sang POST parameters
                params.put("userName", form.getUserName());
                params.put("firstName", form.getFirstName());
                params.put("lastName", form.getLastName());
                params.put("email", form.getEmail());
                params.put("password", form.getPassword());

                // Optional fields
                if (form.getDateOfBirth() != null && !form.getDateOfBirth().isEmpty()) {
                    params.put("dateOfBirth", form.getDateOfBirth());
                }
                if (form.getGender() != null && !form.getGender().isEmpty()) {
                    params.put("gender", form.getGender());
                }
                if (form.getPhone() != null && !form.getPhone().isEmpty()) {
                    params.put("phone", form.getPhone());
                }
                if (form.getAddress() != null && !form.getAddress().isEmpty()) {
                    params.put("address", form.getAddress());
                }

                Log.d(TAG, "Form data: " + params.size() + " fields");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                if (avatarFile != null && avatarFile.exists()) {
                    try {
                        byte[] imageData = readFileToBytes(avatarFile);
                        String fileName = avatarFile.getName();
                        String mimeType = getMimeType(fileName);

                        params.put("avatar", new DataPart(fileName, imageData, mimeType));
                        Log.d(TAG, "Avatar: " + fileName + " (" + imageData.length + " bytes, " + mimeType + ")");
                    } catch (Exception e) {
                        Log.e(TAG, "✗ Error reading avatar: " + e.getMessage(), e);
                    }
                } else {
                    Log.d(TAG, "No avatar, using default");
                }

                return params;
            }
        };

        requestQueue.add(multipartRequest);
        Log.d(TAG, "Request added to Volley queue");
    }

    /**
     * Đọc file thành byte array
     */
    private byte[] readFileToBytes(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        fis.close();
        return bos.toByteArray();
    }

    /**
     * Lấy MIME type từ tên file
     */
    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "image/jpeg";
        }
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
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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

    public interface RegisterCallback {
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
