package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerUnitTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    private User validUser() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }


    @Test
    void shouldAddUserWhenDataIsValid() {
        User user = userController.addUser(validUser());

        assertNotNull(user.getId());
        assertEquals("user@mail.ru", user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        User user = validUser();
        user.setEmail("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        assertEquals(
                "Почта не может быть пустой и должна содержать \"@\"",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotContainAtSymbol() {
        User user = validUser();
        user.setEmail("mail.ru");

        assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpace() {
        User user = validUser();
        user.setLogin("bad login");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        assertEquals(
                "Логин не может быть пустым и не может содержать пробел",
                exception.getMessage()
        );
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        User user = validUser();
        user.setName("");

        User result = userController.addUser(user);

        assertEquals("login", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        User user = validUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.addUser(user)
        );

        assertEquals(
                "Дата дня рождения не может быть в будущем",
                exception.getMessage()
        );
    }


    @Test
    void shouldUpdateUserWhenIdExists() {
        User created = userController.addUser(validUser());

        User updated = new User();
        updated.setId(created.getId());
        updated.setEmail("new@mail.ru");
        updated.setLogin("newlogin");
        updated.setName("Новое имя");
        updated.setBirthday(LocalDate.of(1999, 1, 1));

        User result = userController.updateUser(updated);

        assertEquals("new@mail.ru", result.getEmail());
        assertEquals("newlogin", result.getLogin());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithoutId() {
        User user = validUser();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(user)
        );

        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        User user = validUser();
        user.setId(999L);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userController.updateUser(user)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
    }


    @Test
    void shouldPatchUserEmail() {
        User created = userController.addUser(validUser());

        User patch = new User();
        patch.setId(created.getId());
        patch.setEmail("patch@mail.ru");

        User result = userController.redactUser(patch);

        assertEquals("patch@mail.ru", result.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenPatchWithoutId() {
        User patch = new User();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.redactUser(patch)
        );

        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenPatchUserNotFound() {
        User patch = new User();
        patch.setId(999L);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userController.redactUser(patch)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
    }


    @Test
    void shouldReturnAllUsers() {
        userController.addUser(validUser());
        userController.addUser(validUser());

        assertEquals(2, userController.getAllUsers().size());
    }
}
