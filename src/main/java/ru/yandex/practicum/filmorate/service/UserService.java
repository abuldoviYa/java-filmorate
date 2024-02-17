package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {
    @Getter
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
        userStorage.getUserById(friendId);
        Map<Integer, Boolean> userFriends = user.getFriends();

        userFriends.put(friendId, false);
        user.setFriends(userFriends);
        userStorage.updateUser(user);
    }

    public UserDTO getUserById(Integer userId) {
        return userMapper.toDTO(userStorage.getUserById(userId));
    }


    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        Map<Integer, Boolean> userFriends = user.getFriends();
        userFriends.remove(friendId);
        user.setFriends(userFriends);
        userStorage.updateUser(user);
    }

    public List<UserDTO> getUserFriends(Map<Integer, Boolean> ids) {
        List<User> friends = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : ids.entrySet()) {
            friends.add(userStorage.getUserById(entry.getKey()));
        }
        return friends.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    public List<UserDTO> getCommonFriends(Integer userId, Integer otherUserId) {
        Map<Integer, Boolean> userFriendsIds = userStorage.getUserById(userId).getFriends();
        Map<Integer, Boolean> otherIdFriendsIds = userStorage.getUserById(otherUserId).getFriends();
        if (Objects.nonNull(userFriendsIds) && Objects.nonNull(otherIdFriendsIds)) {
            return addCommonFriends(userFriendsIds, otherIdFriendsIds).stream().map(userMapper::toDTO).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<UserDTO> getFriends(Map<Integer, Boolean> ids) {
        List<User> friends = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : ids.entrySet()) {
            friends.add(userStorage.getUserById(entry.getKey()));
        }
        return friends.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    private List<User> addCommonFriends
            (Map<Integer, Boolean> userFriendsIds, Map<Integer, Boolean> otherIdFriendsIds) {
        List<User> commonFriends = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : userFriendsIds.entrySet()) {
            if (otherIdFriendsIds.containsKey(entry.getKey())) {
                commonFriends.add(userStorage.getUserById(entry.getKey()));
            }
        }
        return commonFriends;
    }

}
