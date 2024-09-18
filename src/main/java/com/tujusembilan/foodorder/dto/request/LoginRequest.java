package com.tujusembilan.foodorder.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotEmpty(message = "{username.required}")
    @Size(max = 100, message = "{username.format}")
    private String username;

    @NotEmpty(message = "{password.required}")
    @Size(min = 6, message = "{password.length}")
    private String password;
}
