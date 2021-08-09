package com.muhardin.endy.belajar.gsuite.dto;

import lombok.Data;

@Data
public class CreateUserResult {
    private Boolean success = true;
    private String errorMessage;
    private String generatedEmail;
    private String generatedPassword;
}
