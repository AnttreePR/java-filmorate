package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerUnitTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    private Film validFilm() {
        Film film = new Film();
        film.setName("Matrix");
        film.setDescription("Sci-fi");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(120);
        return film;
    }


    @Test
    void shouldAddFilm() {
        Film film = filmController.addFilm(validFilm());

        assertNotNull(film.getId());
        assertEquals("Matrix", film.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        Film film = validFilm();
        film.setName("");

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );

        assertEquals("Название фильма не может быть пустым", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {
        Film film = validFilm();
        film.setDescription("a".repeat(201));

        assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
    }


    @Test
    void shouldUpdateFilm() {
        Film film = filmController.addFilm(validFilm());

        Film updated = new Film();
        updated.setId(film.getId());
        updated.setName("Matrix Reloaded");
        updated.setDescription("New");
        updated.setReleaseDate(LocalDate.of(2003, 5, 15));
        updated.setDuration(130);

        Film result = filmController.updateFilm(updated);

        assertEquals("Matrix Reloaded", result.getName());
        assertEquals(130, result.getDuration());
    }

    @Test
    void shouldThrowWhenUpdatingUnknownFilm() {
        Film film = validFilm();
        film.setId(999L);

        assertThrows(
                NotFoundException.class,
                () -> filmController.updateFilm(film)
        );
    }


    @Test
    void shouldPatchFilmName() {
        Film film = filmController.addFilm(validFilm());

        Film patch = new Film();
        patch.setId(film.getId());
        patch.setName("New name");

        Film result = filmController.redactFilm(patch);

        assertEquals("New name", result.getName());
    }

    @Test
    void shouldThrowWhenPatchWithoutId() {
        Film patch = new Film();

        assertThrows(
                ValidationException.class,
                () -> filmController.redactFilm(patch)
        );
    }


    @Test
    void shouldReturnAllFilms() {
        filmController.addFilm(validFilm());
        filmController.addFilm(validFilm());

        assertEquals(2, filmController.getAllFilms().size());
    }
}
