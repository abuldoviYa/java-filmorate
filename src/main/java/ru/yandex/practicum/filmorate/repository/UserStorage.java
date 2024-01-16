package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.DTO.UserDTO;

import java.util.List;

public interface UserStorage {

    UserDTO updateUser(UserDTO updatedUserDTO);

    List<UserDTO> getAllUsers();

    UserDTO createUser(UserDTO userDTO);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    List<UserDTO> getUserFriends(Integer userId);

    List<UserDTO> getCommonFriends(Integer userId, Integer otherUserId);

    UserDTO getUserById(Integer userId);
}
