package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {

    private final UserStorage userStorage;

    private final UserMapper userMapper;

    public UserDTO updateUser(UserDTO updatedUserDTO) {
        return userMapper.toDTO(userStorage.updateUser(userMapper.toModel(updatedUserDTO)));
    }

    public List<UserDTO> getAllUsers() {
        return userStorage.getAllUsers().stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        return userMapper.toDTO(userStorage.createUser(userMapper.toModel(userDTO)));
    }

    public void addFriend(Integer userId, Integer friendId) {

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            log.info("Added {} as a friend to user {}", friendId, userId);
        }
    }

    public UserDTO getUserById(Integer userId) {
        return userMapper.toDTO(userStorage.getUserById(userId));
    }


    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Removed {} from friends of user {}", friendId, userId);
    }

    public List<UserDTO> getUserFriends(Integer userId) {
        User user = userStorage.getUserById(userId);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getCommonFriends(Integer userId, Integer otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);

        List<Integer> commonFriends = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toList());

        return commonFriends.stream()
                .map(userStorage::getUserById)
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

}
