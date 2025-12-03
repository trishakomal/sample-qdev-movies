package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy matey! Search for movies in our treasure chest using various search criteria.
     * This endpoint be the main search functionality that accepts query parameters for
     * name, id, and genre to help ye find the perfect movie treasure!
     * 
     * @param name Movie name to search for (optional, case-insensitive partial match)
     * @param id Movie ID to search for (optional, exact match)
     * @param genre Movie genre to search for (optional, case-insensitive partial match)
     * @param model Spring Model to pass data to the view
     * @return View name for displaying search results
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Arrr! Captain's orders received for movie search - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        // Check if any search criteria were provided, ye landlubber!
        if ((name == null || name.trim().isEmpty()) && 
            id == null && 
            (genre == null || genre.trim().isEmpty())) {
            
            logger.info("No search criteria provided, showing all movies in the treasure chest");
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("searchPerformed", false);
            model.addAttribute("searchMessage", "Ahoy! Use the search form above to find yer favorite movies, matey!");
            return "movies";
        }
        
        // Perform the treasure hunt with the provided search criteria
        List<Movie> searchResults = movieService.searchMovies(name, id, genre);
        
        // Prepare the search message based on results, arrr!
        String searchMessage;
        if (searchResults.isEmpty()) {
            searchMessage = "Shiver me timbers! No movies found matching yer search criteria. " +
                          "Try different search terms, ye scurvy dog!";
            logger.warn("No movies found for search criteria - name: '{}', id: {}, genre: '{}'", 
                       name, id, genre);
        } else {
            searchMessage = String.format("Arrr! Found %d movie treasure%s matching yer search, matey!", 
                                        searchResults.size(), 
                                        searchResults.size() == 1 ? "" : "s");
            logger.info("Search successful! Found {} movies matching the criteria", searchResults.size());
        }
        
        // Add search criteria to model for form persistence
        model.addAttribute("searchName", name != null ? name.trim() : "");
        model.addAttribute("searchId", id);
        model.addAttribute("searchGenre", genre != null ? genre.trim() : "");
        model.addAttribute("movies", searchResults);
        model.addAttribute("searchPerformed", true);
        model.addAttribute("searchMessage", searchMessage);
        
        return "movies";
    }
}