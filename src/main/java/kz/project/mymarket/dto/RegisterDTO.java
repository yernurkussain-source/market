package kz.project.mymarket.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RegisterDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullname;
}