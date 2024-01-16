package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.DTO.UserDTO;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final UserMapper userMapper;
    private final Map<Integer, User> userMap = new HashMap<>();
    private int nextUserId = 1;

    public UserDTO updateUser(UserDTO updatedUserDTO) {
        User updatedUser = userMapper.toModel(updatedUserDTO);

        Integer userId = updatedUser.getId();

        if (userMap.containsKey(userId)) {

            if (updatedUser.getName().isBlank()) {
                updatedUser.setName(updatedUser.getLogin());
            }
            updatedUser.setId(userId);
            userMap.put(userId, updatedUser);
            log.info("User updated");
            return userMapper.toDTO(updatedUser);
        } else {
            log.warn("User id not found");
            throw new ValidationException("User id does not exists", HttpStatus.NOT_FOUND);
        }
    }

    public List<UserDTO> getAllUsers() {
        return userMap.values().stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toModel(userDTO);
        Integer userId = generateNextUserId();
        user.setId(userId);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        userMap.put(userId, user);
        log.info("User created");
        return userMapper.toDTO(user);
    }

    public void addFriend(Integer userId, Integer friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        User user = userMap.get(userId);
        User friend = userMap.get(friendId);

        if (!user.getFriends().contains(friendId)) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            log.info("Added {} as a friend to user {}", friendId, userId);
        }
    }

    public void removeFriend(Integer userId, Integer friendId) {
        validateUserExists(userId);
        validateUserExists(friendId);

        User user = userMap.get(userId);
        User friend = userMap.get(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Removed {} from friends of user {}", friendId, userId);
    }

    public List<UserDTO> getUserFriends(Integer userId) {
        validateUserExists(userId);

        User user = userMap.get(userId);
        return user.getFriends().stream()
                .map(friendId -> userMap.get(friendId))
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getCommonFriends(Integer userId, Integer otherUserId) {
        validateUserExists(userId);
        validateUserExists(otherUserId);

        User user = userMap.get(userId);
        User otherUser = userMap.get(otherUserId);

        List<Integer> commonFriends = user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .collect(Collectors.toList());

        return commonFriends.stream()
                .map(friendId -> userMap.get(friendId))
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Integer userId) {
        validateUserExists(userId);

        User user = userMap.get(userId);
        return userMapper.toDTO(user);
    }

    private void validateUserExists(Integer userId) {
        if (!userMap.containsKey(userId)) {
            log.warn("User with id {} not found", userId);
            throw new ValidationException("User not found", HttpStatus.NOT_FOUND);
        }
    }


    private synchronized int generateNextUserId() {
        return nextUserId++;
    }
}
