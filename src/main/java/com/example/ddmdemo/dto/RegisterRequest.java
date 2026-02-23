package com.example.ddmdemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Korisničko ime je obavezno")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Lozinka je obavezna")
    @Size(min = 4, message = "Lozinka mora imati najmanje 4 karaktera")
    private String password;

    private String role = "USER";
}
