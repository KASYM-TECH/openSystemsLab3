package tim.labs.labs.controller;


import tim.labs.labs.database.entity.Movie;
import tim.labs.labs.database.entity.enums.MovieGenre;
import tim.labs.labs.database.entity.enums.MpaaRating;
import tim.labs.labs.service.MovieService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/movies")
@AllArgsConstructor
public class MovieController {
    private MovieService movieService;
    private WebSocketController webSocketController;

    @PostMapping()
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        try {
            var res = ResponseEntity.ok(movieService.createMovie(movie));
            webSocketController.update("");
            return res;
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies(
            @RequestParam(value = "page", defaultValue = "0") int pageNumber,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortField,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "filterName", defaultValue = "") String filterName,
            @RequestParam(value = "filterTagline", defaultValue = "") String filterTagline,
            @RequestParam(value = "filterMpaaRating", defaultValue = "") MpaaRating mpaaRaring,
            @RequestParam(value = "filterGenre", defaultValue = "") MovieGenre filterGenre) throws Exception {

        List<Movie> movies = movieService.getAllMovies(pageNumber, sortField, pageSize, filterName, filterTagline, filterGenre, mpaaRaring);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCount() throws Exception {
        return ResponseEntity.ok(movieService.Count());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable(name = "id") Long id) throws Exception {
        return movieService.getMovieById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new Exception("Movie not found"));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie, HttpServletRequest request) {
        try {
            var movieUpdated = ResponseEntity.ok(movieService.updateMovie(id, movie, request.getHeader("Authorization")));
            webSocketController.update("");
            return movieUpdated;
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        var ok = movieService.deleteMovie(id);
        if(!ok) {
            return ResponseEntity.badRequest().build();
        }
        webSocketController.update("");
        return ResponseEntity.noContent().build();
    }
}
