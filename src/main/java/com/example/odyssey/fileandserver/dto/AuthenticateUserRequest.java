package com.example.odyssey.fileandserver.dto;

import lombok.Data;

@Data
public class AuthenticateUserRequest {
    private String email;
    private String password;
}
