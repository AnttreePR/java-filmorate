package ru.yandex.practicum.filmorate.model;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class User {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
}
