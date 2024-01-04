package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserValidator userValidator;
    private final Map<Integer, User> userMap = new HashMap<>();
    private int nextUserId = 1;

    public User updateUser(User updatedUser) {
        userValidator.validate(updatedUser);

        Integer userId = updatedUser.getId();

        if (userMap.containsKey(userId)) {

            if (updatedUser.getName().isBlank()){
                updatedUser.setName(updatedUser.getLogin());
            }
            updatedUser.setId(userId);
            userMap.put(userId, updatedUser);
            log.info("User updated");
            return updatedUser;
        } else {
            log.warn("User id not found");
            throw new ValidationException("User id does not exists");
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User createUser(User user) {
        userValidator.validate(user);

        Integer userId = generateNextUserId();
        user.setId(userId);

        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }

        userMap.put(userId, user);
        log.info("User created");
        return user;
    }

    private synchronized int generateNextUserId() {
        return nextUserId++;
    }
}
