package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.DTO.UserDTO;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {

    private final UserStorage userStorage;

    public UserDTO updateUser(UserDTO updatedUserDTO) {
        return userStorage.updateUser(updatedUserDTO);
    }

    public List<UserDTO> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public UserDTO createUser(UserDTO userDTO) {
        return userStorage.createUser(userDTO);
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<UserDTO> getUserFriends(Integer userId) {
        return userStorage.getUserFriends(userId);
    }

    public UserDTO getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public List<UserDTO> getCommonFriends(Integer userId, Integer otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}
