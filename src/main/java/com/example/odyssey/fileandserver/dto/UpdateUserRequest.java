package com.example.odyssey.fileandserver.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String userID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
