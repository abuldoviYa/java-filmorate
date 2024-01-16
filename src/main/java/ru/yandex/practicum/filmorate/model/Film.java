package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateConstraint;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Name should not be blank")
    private String name;
    @Size(max = 200, message = "Description is too long")
    private String description;
    @FilmReleaseDateConstraint
    private LocalDate releaseDate;
    @Min(value = 1)
    private int duration;
    private Set<Integer> likedUserIds;
}
