package com.tujusembilan.foodorder.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotEmpty(message = "{username.required}")
    @Size(max = 100, message = "{username.format")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "{username.format}")
    private String username;

    @NotEmpty(message = "{fullname.required}")
    @Size(max = 255, message = "{fullname.format}")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "{fullname.format}")
    private String fullname;

    @NotEmpty(message = "{password.required}")
    @Size(max = 50, min = 6, message = "{password.length}")
    private String password;

    @NotEmpty(message = "{retypePassword.required}")
    @Size(max = 50, min = 6, message = "{password.length}")
    private String retypePassword;
}
