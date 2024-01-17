package ru.yandex.practicum.filmorate.repository;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User updateUser(User updatedUser);

    List<User> getAllUsers();

    User createUser(User User);

    User getUserById(Integer userId);
}
