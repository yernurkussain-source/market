package kz.project.mymarket.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;
}