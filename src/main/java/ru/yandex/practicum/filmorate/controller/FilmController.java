package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/films")
public class FilmController {
    private long nextId = 1;

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private final static LocalDate CINEMA_BIRTH_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new HashMap<>();


    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("addFilm: название фильма пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("addFilm: описание фильма длиннее 200 символов, length={}", film.getDescription().length());
            throw new ValidationException("Описание фильма не может быть длинней 200 символов");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTH_DATE)) {
            log.error("addFilm: дата релиза раньше допустимой, releaseDate={}",
                    film.getReleaseDate());
            throw new ValidationException("Фильм не может быть издан раньше 28.11.1895");
        }
        if (film.getDuration() <= 0) {
            log.error("addFilm: продолжительность фильма некорректна, duration={}", film.getDuration());
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }

        film.setId(getNextId());

        films.put(film.getId(), film);
        return film;

    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("updateFilm: пустой Id");
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            if (newFilm.getName() == null || newFilm.getName().isEmpty()) {
                log.error("updateFilm: название фильма пустое");
                throw new ValidationException("Название фильма не может быть пустым");
            }
            if (newFilm.getDescription().length() > 200) {
                log.error("updateFilm: описание фильма длиннее 200 символов, length={}", newFilm.getDescription().length());
                throw new ValidationException("Описание фильма не может быть длинней 200 символов");
            }
            if (newFilm.getReleaseDate().isBefore(CINEMA_BIRTH_DATE)) {
                log.error("updateFilm: дата релиза раньше допустимой, releaseDate={}", newFilm.getReleaseDate());
                throw new ValidationException("Фильм не может быть издан раньше 28.11.1895");
            }
            if (newFilm.getDuration() <= 0) {
                log.error("updateFilm: продолжительность фильма некорректна, duration={}", newFilm.getDuration());
                throw new ValidationException("Продолжительность фильма не может быть отрицательной");
            }
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            films.put(oldFilm.getId(), oldFilm);
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");

    }

    @PatchMapping
    public Film redactFilm(@RequestBody Film newFilm) {

        if (newFilm.getId() == null) {
            log.error("redactFilm: id не указан");
            throw new ValidationException("Id должен быть указан");
        }

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.error("redactFilm: фильм не найден, id={}", newFilm.getId());
            throw new ValidationException("Фильм с таким id не найден");
        }

        if (newFilm.getName() != null) {
            if (newFilm.getName().isEmpty()) {
                log.error("redactFilm: пустое название, id={}", newFilm.getId());
                throw new ValidationException("Название фильма не может быть пустым");
            }
            log.info("redactFilm: обновление name, id={}, value={}",
                    newFilm.getId(), newFilm.getName());
            oldFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null) {
            if (newFilm.getDescription().length() > 200) {
                log.error("redactFilm: слишком длинное описание, id={}, length={}",
                        newFilm.getId(), newFilm.getDescription().length());
                throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
            }
            log.info("redactFilm: обновление description, id={}",
                    newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null) {
            if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.error("redactFilm: некорректная дата релиза, id={}, releaseDate={}",
                        newFilm.getId(), newFilm.getReleaseDate());
                throw new ValidationException("Фильм не может быть издан раньше 28.12.1895");
            }
            log.info("redactFilm: обновление releaseDate, id={}, value={}",
                    newFilm.getId(), newFilm.getReleaseDate());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null) {
            if (newFilm.getDuration() <= 0) {
                log.error("redactFilm: некорректная продолжительность, id={}, duration={}",
                        newFilm.getId(), newFilm.getDuration());
                throw new ValidationException("Продолжительность фильма должна быть положительной");
            }
            log.info("redactFilm: обновление duration, id={}, value={}",
                    newFilm.getId(), newFilm.getDuration());
            oldFilm.setDuration(newFilm.getDuration());
        }

        return oldFilm;
    }


    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }


    private long getNextId() {
        return nextId++;
    }
}
