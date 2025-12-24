package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/users")
public class UserController {
    private long nextId = 1;

    private static final  Logger log = LoggerFactory.getLogger(UserController.class);

    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@RequestBody User user) {

        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("addUser: некорректный email, value={}", user.getEmail());
            throw new ValidationException("Почта не может быть пустой и должна содержать \"@\"");
        }

        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("addUser: некорректный login, value={}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и не может содержать пробел");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("addUser: дата рождения в будущем, birthday={}", user.getBirthday());
            throw new ValidationException("Дата дня рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("addUser: имя не указано, будет использован login, login={}", user.getLogin());
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("addUser: пользователь создан, id={}", user.getId());

        return user;
    }


    @PutMapping
    public User updateUser(@RequestBody User newUser) {

        if (newUser.getId() == null) {
            log.error("updateUser: id не указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.error("updateUser: пользователь не найден, id={}", newUser.getId());
            throw new NotFoundException("Пользователь не найден");
        }

        User oldUser = users.get(newUser.getId());

        if (newUser.getEmail() == null || newUser.getEmail().isEmpty() || !newUser.getEmail().contains("@")) {
            log.error("updateUser: некорректный email, id={}, value={}",
                    newUser.getId(), newUser.getEmail());
            throw new ValidationException("Почта не может быть пустой и должна содержать \"@\"");
        }

        if (newUser.getLogin() == null || newUser.getLogin().isEmpty() || newUser.getLogin().contains(" ")) {
            log.error("updateUser: некорректный login, id={}, value={}",
                    newUser.getId(), newUser.getLogin());
            throw new ValidationException("Логин не может быть пустым и не может содержать пробел");
        }

        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.error("updateUser: дата рождения в будущем, id={}, birthday={}",
                    newUser.getId(), newUser.getBirthday());
            throw new ValidationException("Дата дня рождения не может быть в будущем");
        }

        if (newUser.getName() == null || newUser.getName().isEmpty()) {
            log.info("updateUser: имя не указано, будет использован login, id={}", newUser.getId());
            newUser.setName(newUser.getLogin());
        }

        oldUser.setEmail(newUser.getEmail());
        oldUser.setName(newUser.getName());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());

        log.info("updateUser: пользователь обновлён полностью, id={}", newUser.getId());

        return oldUser;
    }


    @PatchMapping
    public User redactUser(@RequestBody User newUser) {

        if (newUser.getId() == null) {
            log.error("redactUser: id не указан");
            throw new ValidationException("Id должен быть указан");
        }

        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.error("redactUser: пользователь не найден, id={}", newUser.getId());
            throw new NotFoundException("Пользователь не найден");
        }

        if (newUser.getEmail() != null) {
            if (newUser.getEmail().isEmpty() || !newUser.getEmail().contains("@")) {
                log.error("redactUser: некорректный email, id={}, value={}",
                        newUser.getId(), newUser.getEmail());
                throw new ValidationException("Почта не может быть пустой и должна содержать \"@\"");
            }
            log.info("redactUser: обновление email, id={}", newUser.getId());
            oldUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null) {
            if (newUser.getLogin().isEmpty() || newUser.getLogin().contains(" ")) {
                log.error("redactUser: некорректный login, id={}, value={}",
                        newUser.getId(), newUser.getLogin());
                throw new ValidationException("Логин не может быть пустым и не может содержать пробелы");
            }
            log.info("redactUser: обновление login, id={}", newUser.getId());
            oldUser.setLogin(newUser.getLogin());
        }

        if (newUser.getBirthday() != null) {
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.error("redactUser: дата рождения в будущем, id={}, birthday={}",
                        newUser.getId(), newUser.getBirthday());
                throw new ValidationException("Дата дня рождения не может быть в будущем");
            }
            log.info("redactUser: обновление birthday, id={}", newUser.getId());
            oldUser.setBirthday(newUser.getBirthday());
        }

        if (newUser.getName() != null) {
            if (newUser.getName().isEmpty()) {
                log.info("redactUser: имя очищено, будет использован login, id={}", newUser.getId());
                oldUser.setName(oldUser.getLogin());
            } else {
                log.info("redactUser: обновление name, id={}", newUser.getId());
                oldUser.setName(newUser.getName());
            }
        }

        return oldUser;
    }


    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }


    private long getNextId() {
        return nextId++;
    }
}
