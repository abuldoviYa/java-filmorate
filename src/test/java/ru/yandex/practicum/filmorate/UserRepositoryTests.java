package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.repository.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTests {
    private final UserDbStorage userStorage;

    private final UserService userService;
    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private Film firstFilm;
    private Film secondFilm;
    private Film thirdFilm;

    @BeforeEach
    public void beforeEach() {
        firstUser = User.builder()
                .name("MisterFirst")
                .login("First")
                .email("1@ya.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();

        secondUser = User.builder()
                .name("MisterSecond")
                .login("Second")
                .email("2@ya.ru")
                .birthday(LocalDate.of(1980, 12, 24))
                .build();

        thirdUser = User.builder()
                .name("MisterThird")
                .login("Third")
                .email("3@ya.ru")
                .birthday(LocalDate.of(1980, 12, 25))
                .build();

        firstFilm = Film.builder()
                .name("Breakfast at Tiffany's")
                .description("American romantic comedy film directed by Blake Edwards, written by George Axelrod," +
                        " adapted from Truman Capote's 1958 novella of the same name.")
                .releaseDate(LocalDate.of(1961, 10, 5))
                .duration(114)
                .build();
        firstFilm.setRating(new Rating(1, "G"));
        firstFilm.setLikedUserIds(new HashSet<>());
        firstFilm.setGenres(Arrays.asList(new Genre(2, "Драма"),
                new Genre(1, "Комедия")));

        secondFilm = Film.builder()
                .name("Avatar")
                .description("American epic science fiction film directed, written, produced, and co-edited" +
                        " by James Cameron. It is set in the mid-22nd century when humans are colonizing Pandora...")
                .releaseDate(LocalDate.of(2009, 12, 10))
                .duration(162)
                .build();
        secondFilm.setRating(new Rating(3, "PG-13"));
        secondFilm.setLikedUserIds(new HashSet<>());
        secondFilm.setGenres(Arrays.asList(new Genre(6, "Боевик")));

        thirdFilm = Film.builder()
                .name("One Flew Over the Cuckoo's Nest")
                .description("American psychological comedy drama film directed by Milos Forman, based on" +
                        " the 1962 novel of the same name by Ken Kesey. The film stars Jack Nicholson...")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .build();
        thirdFilm.setRating(new Rating(4, "R"));
        thirdFilm.setLikedUserIds(new HashSet<>());
        thirdFilm.setGenres(Arrays.asList(new Genre(2, "Драма")));
    }

    @Test
    public void createUserAndGetUserById_UserCreatedAndRetrievedById() {
        firstUser = userStorage.createUser(firstUser);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(firstUser.getId()));
        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", firstUser.getId())
                                .hasFieldOrPropertyWithValue("name", "MisterFirst"));
    }

    @Test
    public void getAllUsers_UserListRetrievedSuccessfully() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        List<User> listUsers = userStorage.getAllUsers();
        assertThat(listUsers).contains(firstUser);
        assertThat(listUsers).contains(secondUser);
    }

    @Test
    public void updateUser_UserUpdatedSuccessfully() {
        firstUser = userStorage.createUser(firstUser);
        User updateUser = User.builder()
                .id(firstUser.getId())
                .name("UpdateMisterFirst")
                .login("First")
                .email("1@ya.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();
        Optional<User> testUpdateUser = Optional.ofNullable(userStorage.updateUser(updateUser));
        assertThat(testUpdateUser)
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("name", "UpdateMisterFirst")
                );
    }

    @Test
    public void addFriend_FriendAddedSuccessfully() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        assertThat(userService.getFriends(userService.getUserStorage().getUserById(firstUser.getId()).getFriends())).hasSize(1);
        assertThat(userService.getUserStorage().getUserById(firstUser.getId()).getFriends()).containsKeys(secondUser.getId());
    }

    @Test
    public void deleteFriend_FriendDeletedSuccessfully() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.removeFriend(firstUser.getId(), secondUser.getId());
        assertThat(userService.getFriends(userService.getUserStorage().getUserById(firstUser.getId()).getFriends())).hasSize(1);
        assertThat(userService.getUserStorage().getUserById(firstUser.getId()).getFriends()).containsKeys(thirdUser.getId());
    }

    @Test
    public void getFriends_FriendsListRetrievedSuccessfully() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        assertThat(userService.getUserStorage().getUserById(firstUser.getId()).getFriends()).hasSize(2);
        assertThat(userService.getUserStorage().getUserById(firstUser.getId()).getFriends()).containsKeys(secondUser.getId(), thirdUser.getId());
    }

    @Test
    public void getCommonFriends_CommonFriendsRetrievedSuccessfully() {
        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);
        userService.addFriend(firstUser.getId(), secondUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.addFriend(secondUser.getId(), firstUser.getId());
        userService.addFriend(secondUser.getId(), thirdUser.getId());
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId())).hasSize(1);
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId())
                .contains(thirdUser));
    }
}