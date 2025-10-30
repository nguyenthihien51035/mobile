package com.example.btl_nhom1.app.domain.model;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @SerializedName(value = "id", alternate = {"userId", "accountId", "user_id", "account_id"})
    private int id;
    @SerializedName(value = "username", alternate = {"account", "userName", "user_name"})
    private String username;

    @SerializedName(value = "email", alternate = {"userEmail", "user_email"})
    private String email;

    @SerializedName(value = "fullName", alternate = {"full_name", "name"})
    private String fullName;

    @SerializedName(value = "firstname", alternate = {"firstName", "first_name"})
    private String firstname;

    @SerializedName(value = "lastname", alternate = {"lastName", "last_name"})
    private String lastname;

    @SerializedName(value = "phone", alternate = {"phoneNumber", "phone_number"})
    private String phone;

    @SerializedName(value = "dateOfBirth", alternate = {"date_of_birth", "birthDate", "dob"})
    private String dateOfBirth;

    @SerializedName(value = "status", alternate = {"accountStatus", "account_status"})
    private String status;

    @SerializedName(value = "address", alternate = {"userAddress", "user_address"})
    private String address;

    @SerializedName(value = "gender")
    private String gender;

    @SerializedName(value = "avatar", alternate = {"avatarUrl", "avatar_url", "image"})
    private String avatar;

    @SerializedName(value = "token", alternate = {"authToken", "auth_token", "accessToken"})
    private String token;

}
