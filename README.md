# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a fun pirate twist!

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🏴‍☠️ Movie Search & Filtering**: Ahoy matey! Search the treasure chest of movies by name, ID, or genre with our pirate-themed search interface
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **Pirate Language**: Arrr! Enjoy the swashbuckling pirate language throughout the search experience

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🏴‍☠️ Movie Search**: http://localhost:8080/movies/search (with optional query parameters)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Movie list with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Unit tests including search functionality
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information, including a pirate-themed search form.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### 🏴‍☠️ Search Movies (NEW!)
```
GET /movies/search
```
Ahoy matey! Search the movie treasure chest using various criteria. Returns an HTML page with filtered movie results and pirate-themed messages.

**Query Parameters (all optional):**
- `name` (string): Movie name to search for (case-insensitive, partial matches allowed)
- `id` (number): Movie ID to search for (exact match, 1-12)
- `genre` (string): Movie genre to search for (case-insensitive, partial matches allowed)

**Examples:**
```bash
# Search by movie name
http://localhost:8080/movies/search?name=prison

# Search by movie ID
http://localhost:8080/movies/search?id=1

# Search by genre
http://localhost:8080/movies/search?genre=drama

# Search with multiple criteria
http://localhost:8080/movies/search?name=family&genre=crime

# Search with no criteria (returns all movies)
http://localhost:8080/movies/search
```

**Search Features:**
- **Case-insensitive**: Search works regardless of capitalization
- **Partial matching**: Find movies with partial name or genre matches
- **Multiple criteria**: Combine name, ID, and genre filters
- **Pirate language**: Enjoy swashbuckling messages and interface
- **Form persistence**: Search criteria are preserved after search
- **Empty result handling**: Friendly pirate messages when no movies are found
- **Input validation**: Handles invalid IDs and empty search terms gracefully

## Search Examples

### By Movie Name
```bash
# Find movies with "the" in the name
curl "http://localhost:8080/movies/search?name=the"

# Case-insensitive search
curl "http://localhost:8080/movies/search?name=PRISON"
```

### By Genre
```bash
# Find all drama movies
curl "http://localhost:8080/movies/search?genre=drama"

# Find action movies
curl "http://localhost:8080/movies/search?genre=action"
```

### Combined Search
```bash
# Find crime dramas
curl "http://localhost:8080/movies/search?genre=crime"

# Find specific movie by name and genre
curl "http://localhost:8080/movies/search?name=family&genre=crime"
```

## Testing

Run the comprehensive test suite including search functionality:

```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=MovieServiceTest
mvn test -Dtest=MoviesControllerTest
```

**Test Coverage:**
- Movie service search methods (by name, genre, multiple criteria)
- Controller search endpoint with various parameters
- Edge cases (empty results, invalid parameters, null values)
- Form persistence and user experience
- Pirate-themed messaging validation

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Check that the application started successfully
2. Verify the movies.json file is present in resources
3. Check logs for any search-related errors
4. Ensure proper URL encoding for search parameters

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the UI/UX with more pirate themes
- Add new search features (by director, year, rating)
- Improve the responsive design
- Add more pirate language and personality
- Implement advanced filtering options

## Recent Updates

### Version 1.1.0 - Pirate Search Feature 🏴‍☠️
- **NEW**: Movie search and filtering API endpoint `/movies/search`
- **NEW**: Pirate-themed search form with name, ID, and genre filters
- **NEW**: Case-insensitive partial matching for names and genres
- **NEW**: Comprehensive error handling and user-friendly messages
- **NEW**: Form persistence to maintain search criteria
- **NEW**: Extensive unit test coverage for search functionality
- **ENHANCED**: Updated documentation with search examples and API details
- **ENHANCED**: Improved user experience with pirate language and theming

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May ye find the perfect movie treasure in our collection, matey! 🏴‍☠️*
