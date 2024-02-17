package ru.yandex.practicum.filmorate.repository;


import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    User updateUser(User updatedUser);

    List<User> getAllUsers();

    User createUser(User user);

    User getUserById(Integer userId);

}
