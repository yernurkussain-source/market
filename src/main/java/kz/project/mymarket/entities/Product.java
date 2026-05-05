package kz.project.mymarket.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Өнім атауы бос болмауы керек")
    private String name;

    @NotNull(message = "Баға бос болмауы керек")
    @Positive(message = "Баға оң сан болуы керек")
    private Double price;

    @NotNull(message = "Саны бос болмауы керек")
    @Min(value = 0, message = "Саны 0-ден кіші болмауы керек")
    private Integer quantity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}