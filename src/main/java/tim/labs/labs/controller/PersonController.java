package tim.labs.labs.controller;


import tim.labs.labs.database.entity.Person;
import tim.labs.labs.database.entity.enums.Color;
import tim.labs.labs.database.entity.enums.Country;
import tim.labs.labs.service.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/persons")
@AllArgsConstructor
public class PersonController {
    private PersonService personService;
    private WebSocketController webSocketController;

    @GetMapping()
    public ResponseEntity<List<Person>> getAll(@RequestParam(value = "page", defaultValue = "0") int pageNumber,
                                                   @RequestParam(value = "sortBy", defaultValue = "id") String sortField,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                   @RequestParam(value = "filterName", defaultValue = "") String name,
                                                   @RequestParam(value = "filterEyeColor", defaultValue = "") Color eyeColor,
                                                   @RequestParam(value = "filterHairColor", defaultValue = "") Color hairColor,
                                                   @RequestParam(value = "filterNationality", defaultValue = "") Country nationality
                                               ) {
        List<Person> persons = personService.getAll(pageNumber, sortField, pageSize, name, eyeColor, hairColor, nationality);
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> get(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok(personService.getPersonById(id));
    }

    @PostMapping()
    public ResponseEntity<Person> create(@RequestBody Person person) {
        try {
            var created = ResponseEntity.ok(personService.createPerson(person));
            webSocketController.update("");
            return created;
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> update(@PathVariable Long id, @RequestBody Person person, HttpServletRequest request) throws Exception {
        try {
            person.setId(id);
            var updated = ResponseEntity.ok(personService.updatePerson(person.getId(), person, request.getHeader("Authorization")));
            webSocketController.update("");
            return updated;
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(name = "replaceId") Long idToReplace) throws Exception {
        var ok = personService.replaceWith(id, idToReplace);
        webSocketController.update("");
        if(!ok) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
}

