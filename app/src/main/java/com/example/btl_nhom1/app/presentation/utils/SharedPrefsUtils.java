package com.example.btl_nhom1.app.presentation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.btl_nhom1.app.domain.model.Account;

public class SharedPrefsUtils {
    // Keys cho các field
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_DATE_OF_BIRTH = "dateOfBirth";
    public static final String KEY_STATUS = "status";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String PREFS_NAME = "user_prefs";

    /**
     * Lấy SharedPreferences instance
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lưu thông tin user sau khi đăng nhập hoặc cập nhật
     */
    public static void saveUserData(Context context, Account account) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();

        Log.d("SharedPrefsUtils", "Saving account - ID: " + account.getId());

        editor.putInt(KEY_USER_ID, account.getId());
        editor.putString(KEY_USERNAME, account.getUsername());
        editor.putString(KEY_EMAIL, account.getEmail());
        editor.putString(KEY_FIRSTNAME, account.getFirstname());
        editor.putString(KEY_LASTNAME, account.getLastname());
        editor.putString(KEY_PHONE, account.getPhone());
        editor.putString(KEY_ADDRESS, account.getAddress());
        editor.putString(KEY_GENDER, account.getGender());
        editor.putString(KEY_AVATAR, account.getAvatar());
        editor.putString(KEY_DATE_OF_BIRTH, account.getDateOfBirth());
        editor.putString(KEY_STATUS, account.getStatus());
        editor.putString(KEY_TOKEN, account.getToken());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        boolean success = editor.commit();

        Log.d("SharedPrefsUtils", "Save result: " + (success ? "SUCCESS" : "FAILED"));

        if (success) {
            int savedId = getSharedPreferences(context).getInt(KEY_USER_ID, -1);
            Log.d("SharedPrefsUtils", "Verified saved ID: " + savedId);
        }
    }

    /**
     * Kiểm tra user đã đăng nhập chưa
     */
    public static boolean isLoggedIn(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Lấy user ID
     */
    public static int getUserId(Context context) {
        return getSharedPreferences(context).getInt(KEY_USER_ID, -1);
    }

    /**
     * Lấy username
     */
    public static String getUsername(Context context) {
        return getSharedPreferences(context).getString(KEY_USERNAME, "");
    }

    /**
     * Lấy email
     */
    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString(KEY_EMAIL, "");
    }

    /**
     * Lấy firstname
     */
    public static String getFirstname(Context context) {
        return getSharedPreferences(context).getString(KEY_FIRSTNAME, "");
    }

    /**
     * Lấy lastname
     */
    public static String getLastname(Context context) {
        return getSharedPreferences(context).getString(KEY_LASTNAME, "");
    }

    /**
     * Lấy full name
     */
    public static String getFullName(Context context) {
        String firstname = getFirstname(context);
        String lastname = getLastname(context);
        return (firstname + " " + lastname).trim();
    }

    /**
     * Lấy phone
     */
    public static String getPhone(Context context) {
        return getSharedPreferences(context).getString(KEY_PHONE, "");
    }

    /**
     * Lấy address
     */
    public static String getAddress(Context context) {
        return getSharedPreferences(context).getString(KEY_ADDRESS, "");
    }

    /**
     * Lấy gender
     */
    public static String getGender(Context context) {
        return getSharedPreferences(context).getString(KEY_GENDER, "");
    }

    /**
     * Lấy avatar
     */
    public static String getAvatar(Context context) {
        return getSharedPreferences(context).getString(KEY_AVATAR, "");
    }

    /**
     * Lấy date of birth
     */
    public static String getDateOfBirth(Context context) {
        return getSharedPreferences(context).getString(KEY_DATE_OF_BIRTH, "");
    }

    /**
     * Lấy status - THÊM MỚI
     */
    public static String getStatus(Context context) {
        return getSharedPreferences(context).getString(KEY_STATUS, "");
    }

    /**
     * Kiểm tra account có active không - THÊM MỚI
     */
    public static boolean isAccountActive(Context context) {
        String status = getStatus(context);
        return "ACTIVE".equalsIgnoreCase(status);
    }

    /**
     * Lấy token
     */
    public static String getToken(Context context) {
        return getSharedPreferences(context).getString(KEY_TOKEN, "");
    }

    /**
     * Đăng xuất - Xóa tất cả dữ liệu user
     */
    public static void logout(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}
