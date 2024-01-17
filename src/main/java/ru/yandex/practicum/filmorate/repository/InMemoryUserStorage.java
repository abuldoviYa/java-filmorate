package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userMap = new HashMap<>();
    private int nextUserId = 1;

    public User updateUser(User updatedUser) {

        Integer userId = updatedUser.getId();

        if (userMap.containsKey(userId)) {

            if (updatedUser.getName().isBlank()) {
                updatedUser.setName(updatedUser.getLogin());
            }
            updatedUser.setId(userId);
            userMap.put(userId, updatedUser);
            log.info("User updated");
            return updatedUser;
        } else {
            log.warn("User id not found");
            throw new ValidationException("User id does not exists", HttpStatus.NOT_FOUND);
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User createUser(User user) {
        Integer userId = generateNextUserId();
        user.setId(userId);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        userMap.put(userId, user);
        log.info("User created");
        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        if (!userMap.containsKey(userId)) {
            log.warn("User with id {} not found", userId);
            throw new ValidationException("User not found", HttpStatus.NOT_FOUND);
        }
        return userMap.get(userId);
    }


    private synchronized int generateNextUserId() {
        return nextUserId++;
    }
}
