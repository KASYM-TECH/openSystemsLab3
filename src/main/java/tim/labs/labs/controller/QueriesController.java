package tim.labs.labs.controller;

import tim.labs.labs.database.entity.GroupByTotalBoxOffice;
import tim.labs.labs.database.entity.Movie;
import tim.labs.labs.database.entity.enums.MovieGenre;
import tim.labs.labs.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/queries")
@AllArgsConstructor
public class QueriesController {
    private MovieService movieService;
    private WebSocketController webSocketController;

    @GetMapping("/minTotalBoxOffice")
    public ResponseEntity<Long> minTotalBoxOffice() {
        return ResponseEntity.ok(movieService.giveMinTotalBoxOffice());
    }

    @GetMapping("/groupByTotalBoxOffice")
    public ResponseEntity<List<GroupByTotalBoxOffice>> groupByTotalBoxOffice() {
        return ResponseEntity.ok(movieService.groupByTotalBoxOffice());
    }

    @PostMapping("/zeroOscarCountByGenre")
    public ResponseEntity<Void> zeroOscarCountByGenre(@RequestParam(value = "genre") MovieGenre genre) {
        movieService.zeroOscarCountByGenre(genre);
        webSocketController.update("");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/allWithNoOscars")
    public ResponseEntity<List<Movie>> getAllWithNoOscars() {
        return ResponseEntity.ok(movieService.getAllWithNoOscars());
    }
}
