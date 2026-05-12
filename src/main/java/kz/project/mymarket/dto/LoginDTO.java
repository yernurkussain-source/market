package kz.project.mymarket.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class LoginDTO {

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}