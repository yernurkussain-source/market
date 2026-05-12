package kz.project.mymarket.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    private Long categoryId;
    private String categoryName;
}