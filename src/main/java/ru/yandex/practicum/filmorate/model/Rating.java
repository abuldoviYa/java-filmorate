package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
public class Rating {
    @NotBlank
    private Integer id;
    @Size(max = 100)
    @NotBlank
    private String name;

    public Rating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}