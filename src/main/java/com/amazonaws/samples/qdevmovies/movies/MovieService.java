package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! Search for movies in our treasure chest using various criteria.
     * This method be the main search crew member that coordinates all search operations.
     * 
     * @param name Movie name to search for (case-insensitive, partial matches allowed)
     * @param id Movie ID to search for
     * @param genre Movie genre to search for (case-insensitive, partial matches allowed)
     * @return List of movies matching the search criteria, or empty list if no treasure be found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Starting treasure hunt with search criteria - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        List<Movie> searchResults = new ArrayList<>(movies);
        
        // Filter by movie ID if provided - this be the most specific search, matey!
        if (id != null && id > 0) {
            logger.debug("Searching by movie ID: {}", id);
            Optional<Movie> movieById = getMovieById(id);
            searchResults = movieById.map(List::of).orElse(new ArrayList<>());
        }
        
        // Filter by movie name if provided - case-insensitive partial match, arrr!
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            logger.debug("Filtering by movie name containing: '{}'", searchName);
            searchResults = searchResults.stream()
                    .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                    .collect(Collectors.toList());
        }
        
        // Filter by genre if provided - case-insensitive partial match, ye scurvy dog!
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            logger.debug("Filtering by genre containing: '{}'", searchGenre);
            searchResults = searchResults.stream()
                    .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                    .collect(Collectors.toList());
        }
        
        logger.info("Treasure hunt complete! Found {} movies matching the search criteria", searchResults.size());
        return searchResults;
    }

    /**
     * Search movies by name only - a specialized crew member for name-based treasure hunting!
     * 
     * @param name Movie name to search for (case-insensitive, partial matches allowed)
     * @return List of movies with names containing the search term
     */
    public List<Movie> searchMoviesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Arrr! Empty search name provided, returning empty treasure chest");
            return new ArrayList<>();
        }
        
        String searchName = name.trim().toLowerCase();
        logger.info("Searching for movies with name containing: '{}'", searchName);
        
        List<Movie> results = movies.stream()
                .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
                
        logger.info("Found {} movies with name matching '{}'", results.size(), searchName);
        return results;
    }

    /**
     * Search movies by genre only - another specialized crew member for genre-based treasure hunting!
     * 
     * @param genre Movie genre to search for (case-insensitive, partial matches allowed)
     * @return List of movies with genres containing the search term
     */
    public List<Movie> searchMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            logger.warn("Arrr! Empty search genre provided, returning empty treasure chest");
            return new ArrayList<>();
        }
        
        String searchGenre = genre.trim().toLowerCase();
        logger.info("Searching for movies with genre containing: '{}'", searchGenre);
        
        List<Movie> results = movies.stream()
                .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                .collect(Collectors.toList());
                
        logger.info("Found {} movies with genre matching '{}'", results.size(), searchGenre);
        return results;
    }
}
