package com.example.btl_nhom1.app.dto.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterForm {

    @NotBlank(message = "Tên đăng nhập không được bỏ trống")
    private String userName;

    @NotBlank(message = "Tên không được bỏ trống")
    private String firstName;

    @NotBlank(message = "Họ không được bỏ trống")
    private String lastName;

    private String dateOfBirth;

    private String gender;

    private String phone;

    private String address;

    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$",
            message = "Mật khẩu phải có 8-16 ký tự, chứa ít nhất: 1 chữ thường, 1 chữ hoa, 1 số, 1 ký tự đặc biệt (@$!%*?&)"
    )
    private String password;

    private String avatar;
}