package com.example.btl_nhom1.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private int id;
    private String username;
    private String email;
    private String fullName;
    private String firstname;
    private String lastname;
    private String phone;
    private String dateOfBirth;
    private String address;
    private String gender;
    private String avatar;
    private String token;
}
