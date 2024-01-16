package ru.yandex.practicum.filmorate.DTO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDTO {
    private Integer id;
    @Email
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "Login is mandatory")
    @Pattern(regexp = "^\\S+$", message = "Login format error")
    private String login;
    private String name;
    @Past(message = "Birth date must be in the past")
    private LocalDate birthday;
    private Set<Integer> friends;
}
