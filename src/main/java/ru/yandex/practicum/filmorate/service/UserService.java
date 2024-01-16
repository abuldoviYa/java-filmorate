package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.DTO.UserDTO;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {

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

    private synchronized int generateNextUserId() {
        return nextUserId++;
    }
}
