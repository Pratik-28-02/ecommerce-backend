package com.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String name;
}
