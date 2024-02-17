package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AppException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Qualifier("UserDbStorage")
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS");
        while (userRows.next()) {
            User user = makeUser(userRows);
            users.add(user);
        }
        return users;
    }

    @Override
    public User getUserById(Integer userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", userId);
        if (userRows.next()) {
            User user = makeUser(userRows);
            return user;
        } else {
            throw new AppException("Нет пользователя с таким id", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public User createUser(User user) {
        user.setName(checkAndReturnName(user));
        String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        Integer userId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return getUserById(userId);
    }

    @Override
    public User updateUser(User user) {
        SqlRowSet userRS = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", user.getId());
        if (userRS.next()) {
            String sqlQuery = "UPDATE USERS set EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where USER_ID = ?";
            jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
            updateFriends(user);
            return user;
        } else throw new AppException("Нет пользователя с таким id", HttpStatus.NOT_FOUND);
    }

    private User makeUser(SqlRowSet rs) {
        Integer userId = rs.getInt("USER_ID");
        String email = rs.getString("EMAIL");
        String login = rs.getString("LOGIN");
        String name = rs.getString("NAME");
        LocalDate birthday = Objects.requireNonNull(rs.getDate("BIRTHDAY")).toLocalDate();
        Map<Integer, Boolean> friends = getFriends(userId);
        return new User(userId, email, login, name, birthday, friends);
    }

    private void updateFriends(User user) {
        deleteFromFriends(user);
        String sqlQuery = "INSERT INTO FRIENDS (FRIEND_1, FRIEND_2, CONFIRMATION) VALUES (?, ?, ?)";
        for (Map.Entry<Integer, Boolean> entry : user.getFriends().entrySet()) {
            jdbcTemplate.update(sqlQuery, user.getId(), entry.getKey(), entry.getValue());
        }
    }

    private Map<Integer, Boolean> getFriends(Integer id) {
        SqlRowSet friendsRows1 = jdbcTemplate.queryForRowSet("SELECT FRIEND_2, CONFIRMATION FROM FRIENDS " +
                "WHERE FRIEND_1 = ?", id);
        HashMap<Integer, Boolean> friends = new HashMap<>(getMapOfFriends(friendsRows1));
        return friends;
    }

    private Map<Integer, Boolean> getMapOfFriends(SqlRowSet rs) {
        HashMap<Integer, Boolean> friends = new HashMap<>();
        while (rs.next()) {
            Integer userId = rs.getInt(1);
            Boolean confirmation = rs.getBoolean(2);
            friends.put(userId, confirmation);
        }
        return friends;
    }

    private void deleteFromUsers(User user) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    private void deleteFromFriends(User user) {
        String sqlQuery = "DELETE FROM FRIENDS WHERE FRIEND_1 = ? OR FRIEND_2 = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), user.getId());
    }

    private void deleteFromFilmUsersLikes(User user) {
        String sqlQuery = "DELETE FROM FILM_USERS_LIKES WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    private String checkAndReturnName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            return user.getLogin();
        } else {
            return user.getName();
        }

    }
}
