package kz.project.mymarket.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email бос болмауы керек")
    @Email(message = "Email форматы дұрыс емес")
    private String email;

    @NotBlank(message = "Пароль бос болмауы керек")
    @Size(min = 4, message = "Пароль кемінде 4 символ болуы керек")
    private String password;

    @NotBlank(message = "Аты-жөні бос болмауы керек")
    private String fullname;
}