package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ahoy matey! Unit tests for the MoviesController including the new search functionality.
 * These tests be ensuring our movie treasure hunting endpoints work ship shape!
 */
public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with enhanced search functionality
        mockMovieService = new MovieService() {
            private final List<Movie> testMovies = Arrays.asList(
                new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                new Movie(3L, "Comedy Film", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
            );
            
            @Override
            public List<Movie> getAllMovies() {
                return testMovies;
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                return testMovies.stream().filter(movie -> movie.getId().equals(id)).findFirst();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> results = new ArrayList<>(testMovies);
                
                if (id != null && id > 0) {
                    Optional<Movie> movieById = getMovieById(id);
                    results = movieById.map(Arrays::asList).orElse(new ArrayList<>());
                }
                
                if (name != null && !name.trim().isEmpty()) {
                    String searchName = name.trim().toLowerCase();
                    results = results.stream()
                            .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                            .collect(java.util.stream.Collectors.toList());
                }
                
                if (genre != null && !genre.trim().isEmpty()) {
                    String searchGenre = genre.trim().toLowerCase();
                    results = results.stream()
                            .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                            .collect(java.util.stream.Collectors.toList());
                }
                
                return results;
            }
            
            @Override
            public List<Movie> searchMoviesByName(String name) {
                if (name == null || name.trim().isEmpty()) {
                    return new ArrayList<>();
                }
                String searchName = name.trim().toLowerCase();
                return testMovies.stream()
                        .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                        .collect(java.util.stream.Collectors.toList());
            }
            
            @Override
            public List<Movie> searchMoviesByGenre(String genre) {
                if (genre == null || genre.trim().isEmpty()) {
                    return new ArrayList<>();
                }
                String searchGenre = genre.trim().toLowerCase();
                return testMovies.stream()
                        .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                        .collect(java.util.stream.Collectors.toList());
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    @DisplayName("Test getting all movies")
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size());
    }

    @Test
    @DisplayName("Test getting movie details with valid ID")
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        Movie movie = (Movie) model.asMap().get("movie");
        assertNotNull(movie);
        assertEquals("Test Movie", movie.getMovieName());
    }

    @Test
    @DisplayName("Test getting movie details with invalid ID")
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        String title = (String) model.asMap().get("title");
        assertEquals("Movie Not Found", title);
    }

    @Test
    @DisplayName("Arrr! Test movie search with no criteria - should show all movies")
    public void testSearchMoviesNoCriteria() {
        String result = moviesController.searchMovies(null, null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(3, movies.size());
        
        Boolean searchPerformed = (Boolean) model.asMap().get("searchPerformed");
        assertEquals(false, searchPerformed);
        
        String searchMessage = (String) model.asMap().get("searchMessage");
        assertTrue(searchMessage.contains("Ahoy!"));
    }

    @Test
    @DisplayName("Test movie search by name")
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("test", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        Boolean searchPerformed = (Boolean) model.asMap().get("searchPerformed");
        assertEquals(true, searchPerformed);
        
        String searchMessage = (String) model.asMap().get("searchMessage");
        assertTrue(searchMessage.contains("Found 1 movie treasure"));
    }

    @Test
    @DisplayName("Test movie search by ID")
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
        
        String searchMessage = (String) model.asMap().get("searchMessage");
        assertTrue(searchMessage.contains("Found 1 movie treasure"));
    }

    @Test
    @DisplayName("Test movie search by genre")
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "comedy", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(1, movies.size());
        assertEquals("Comedy Film", movies.get(0).getMovieName());
        
        String searchMessage = (String) model.asMap().get("searchMessage");
        assertTrue(searchMessage.contains("Found 1 movie treasure"));
    }

    @Test
    @DisplayName("Test movie search with multiple criteria")
    public void testSearchMoviesMultipleCriteria() {
        String result = moviesController.searchMovies("action", null, "action", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
    }

    @Test
    @DisplayName("Shiver me timbers! Test search with no results")
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("nonexistent", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertTrue(movies.isEmpty());
        
        String searchMessage = (String) model.asMap().get("searchMessage");
        assertTrue(searchMessage.contains("Shiver me timbers!"));
        assertTrue(searchMessage.contains("No movies found"));
    }

    @Test
    @DisplayName("Test search with invalid ID")
    public void testSearchMoviesInvalidId() {
        String result = moviesController.searchMovies(null, 999L, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertTrue(movies.isEmpty());
        
        String searchMessage = (String) model.asMap().get("searchMessage");
        assertTrue(searchMessage.contains("No movies found"));
    }

    @Test
    @DisplayName("Test search form persistence - search criteria are preserved in model")
    public void testSearchFormPersistence() {
        String result = moviesController.searchMovies("test movie", 1L, "drama", model);
        assertEquals("movies", result);
        
        assertEquals("test movie", model.asMap().get("searchName"));
        assertEquals(1L, model.asMap().get("searchId"));
        assertEquals("drama", model.asMap().get("searchGenre"));
    }

    @Test
    @DisplayName("Test search with empty strings")
    public void testSearchMoviesEmptyStrings() {
        String result = moviesController.searchMovies("", null, "", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(3, movies.size()); // Should return all movies
        
        Boolean searchPerformed = (Boolean) model.asMap().get("searchPerformed");
        assertEquals(false, searchPerformed);
    }

    @Test
    @DisplayName("Test search with whitespace-only strings")
    public void testSearchMoviesWhitespaceStrings() {
        String result = moviesController.searchMovies("   ", null, "   ", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.asMap().get("movies");
        assertEquals(3, movies.size()); // Should return all movies
        
        Boolean searchPerformed = (Boolean) model.asMap().get("searchPerformed");
        assertEquals(false, searchPerformed);
    }

    @Test
    @DisplayName("Test movie service integration")
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        Optional<Movie> movie = mockMovieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("Test Movie", movie.get().getMovieName());
    }
}