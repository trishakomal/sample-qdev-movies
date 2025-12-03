package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy matey! Unit tests for the MovieService search functionality.
 * These tests be ensuring our treasure hunting methods work ship shape!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    @DisplayName("Arrr! Test searching movies by name - case insensitive partial match")
    public void testSearchMoviesByName() {
        // Test case-insensitive partial match
        List<Movie> results = movieService.searchMoviesByName("prison");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());

        // Test case-insensitive full match
        results = movieService.searchMoviesByName("THE FAMILY BOSS");
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());

        // Test partial match with multiple results
        results = movieService.searchMoviesByName("the");
        assertTrue(results.size() > 1);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getMovieName().toLowerCase().contains("the")));
    }

    @Test
    @DisplayName("Test searching movies by name with empty or null input")
    public void testSearchMoviesByNameEmptyInput() {
        // Test null input
        List<Movie> results = movieService.searchMoviesByName(null);
        assertTrue(results.isEmpty());

        // Test empty string
        results = movieService.searchMoviesByName("");
        assertTrue(results.isEmpty());

        // Test whitespace only
        results = movieService.searchMoviesByName("   ");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Test searching movies by name with no matches")
    public void testSearchMoviesByNameNoMatches() {
        List<Movie> results = movieService.searchMoviesByName("nonexistent movie");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Arrr! Test searching movies by genre - case insensitive partial match")
    public void testSearchMoviesByGenre() {
        // Test case-insensitive partial match
        List<Movie> results = movieService.searchMoviesByGenre("drama");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("drama")));

        // Test case-insensitive full match
        results = movieService.searchMoviesByGenre("ACTION");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("action")));

        // Test specific genre
        results = movieService.searchMoviesByGenre("Crime");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(movie -> 
            movie.getGenre().toLowerCase().contains("crime")));
    }

    @Test
    @DisplayName("Test searching movies by genre with empty or null input")
    public void testSearchMoviesByGenreEmptyInput() {
        // Test null input
        List<Movie> results = movieService.searchMoviesByGenre(null);
        assertTrue(results.isEmpty());

        // Test empty string
        results = movieService.searchMoviesByGenre("");
        assertTrue(results.isEmpty());

        // Test whitespace only
        results = movieService.searchMoviesByGenre("   ");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Test searching movies by genre with no matches")
    public void testSearchMoviesByGenreNoMatches() {
        List<Movie> results = movieService.searchMoviesByGenre("nonexistent genre");
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Shiver me timbers! Test comprehensive movie search with multiple criteria")
    public void testSearchMoviesMultipleCriteria() {
        // Test search by name and genre
        List<Movie> results = movieService.searchMovies("family", null, "crime");
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertTrue(results.get(0).getGenre().toLowerCase().contains("crime"));

        // Test search by ID only (should return single movie or empty)
        results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());

        // Test search with all criteria
        results = movieService.searchMovies("prison", 1L, "drama");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    @DisplayName("Test search with invalid ID")
    public void testSearchMoviesInvalidId() {
        // Test with non-existent ID
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty());

        // Test with negative ID
        results = movieService.searchMovies(null, -1L, null);
        assertTrue(results.isEmpty());

        // Test with zero ID
        results = movieService.searchMovies(null, 0L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Test search with no criteria returns all movies")
    public void testSearchMoviesNoCriteria() {
        List<Movie> allMovies = movieService.getAllMovies();
        List<Movie> searchResults = movieService.searchMovies(null, null, null);
        assertEquals(allMovies.size(), searchResults.size());

        // Test with empty strings
        searchResults = movieService.searchMovies("", null, "");
        assertEquals(allMovies.size(), searchResults.size());
    }

    @Test
    @DisplayName("Test search with conflicting criteria")
    public void testSearchMoviesConflictingCriteria() {
        // Search for a movie with ID 1 but name that doesn't match
        List<Movie> results = movieService.searchMovies("nonexistent", 1L, null);
        // Should return empty because ID search takes precedence but then name filter is applied
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Arrr! Test case sensitivity in search")
    public void testSearchCaseSensitivity() {
        // Test name search case insensitivity
        List<Movie> lowerCase = movieService.searchMoviesByName("prison");
        List<Movie> upperCase = movieService.searchMoviesByName("PRISON");
        List<Movie> mixedCase = movieService.searchMoviesByName("PrIsOn");
        
        assertEquals(lowerCase.size(), upperCase.size());
        assertEquals(lowerCase.size(), mixedCase.size());
        
        // Test genre search case insensitivity
        lowerCase = movieService.searchMoviesByGenre("drama");
        upperCase = movieService.searchMoviesByGenre("DRAMA");
        mixedCase = movieService.searchMoviesByGenre("DrAmA");
        
        assertEquals(lowerCase.size(), upperCase.size());
        assertEquals(lowerCase.size(), mixedCase.size());
    }

    @Test
    @DisplayName("Test getAllMovies returns non-empty list")
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertTrue(movies.size() > 0);
    }

    @Test
    @DisplayName("Test getMovieById with valid and invalid IDs")
    public void testGetMovieById() {
        // Test valid ID
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals(1L, movie.get().getId());

        // Test invalid ID
        movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());

        // Test null ID
        movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());

        // Test negative ID
        movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());
    }
}