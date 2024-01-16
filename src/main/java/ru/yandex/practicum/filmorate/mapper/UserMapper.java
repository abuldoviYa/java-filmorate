package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.DTO.UserDTO;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setLogin(user.getLogin());
        userDTO.setName(user.getName());
        userDTO.setBirthday(user.getBirthday());
        if (user.getFriends() == null) {
            userDTO.setFriends(new HashSet<>());
        } else {
            userDTO.setFriends(user.getFriends());
        }

        return userDTO;
    }


    public User toModel(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setLogin(userDTO.getLogin());
        user.setName(userDTO.getName());
        user.setBirthday(userDTO.getBirthday());
        if (userDTO.getFriends() == null) {
            user.setFriends(new HashSet<>());
        } else {
            user.setFriends(userDTO.getFriends());
        }

        return user;
    }
}
