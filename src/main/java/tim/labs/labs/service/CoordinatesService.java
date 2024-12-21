package tim.labs.labs.service;

import tim.labs.labs.database.entity.Coordinates;
import tim.labs.labs.database.entity.enums.Role;
import tim.labs.labs.database.repository.CoordinatesRepository;
import tim.labs.labs.database.repository.MovieRepository;
import tim.labs.labs.exception.ForbiddenException;
import tim.labs.labs.security.jwt.service.IJwtService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CoordinatesService {
    private CoordinatesRepository coordinatesRepository;
    private MovieRepository movieRepository;
    private IJwtService jwtService;

    public Coordinates createCoordinates(Coordinates coordinates) {
        return coordinatesRepository.save(coordinates);
    }

    public Coordinates getCoordinatesById(Long id) throws Exception {
        return coordinatesRepository.findById(id).orElseThrow(() -> new Exception("Coordinates not found"));
    }

    public List<Coordinates> getAll() {
        return coordinatesRepository.findAll();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Coordinates updateCoordinates(Long id, Coordinates updatedCoordinates, String token) throws Exception {
        var coordinates = coordinatesRepository.findById(id).stream().findFirst().orElseThrow();
        var userRole = jwtService.getUserRoleFromToken(token);
        var userId = jwtService.getUserIdFromToken(token);

        if(coordinates.getCreatorId() != userId) {
            if(userRole != Role.Admin) {
                throw new ForbiddenException();
            }
            if(!coordinates.getAllowAdminEdit()){
                throw new ForbiddenException();
            }
        }

        coordinates.setX(updatedCoordinates.getX());
        coordinates.setY(updatedCoordinates.getY());

        return coordinatesRepository.save(coordinates);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void replaceCoordinates(Long id, Long toReplaceId) {
        if(Objects.equals(id, toReplaceId)) {
            return ;
        }
        movieRepository.replaceCoordinates(id, toReplaceId);
        coordinatesRepository.deleteById(id);
    }

    public List<Coordinates> getAll(int pageNumber, String sortByField, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortByField));
        Page<Coordinates> coordinatesPage = coordinatesRepository.findAll(pageRequest);
        return coordinatesPage.getContent();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int insertCoordinates(List<Coordinates> coordinatesList) {
        var count = 0;
        for (Coordinates coordinate : coordinatesList) {
            if (!coordinate.Validate()) {
                return -1;
            }
            count++;
        }
        coordinatesRepository.saveAll(coordinatesList);
        return count;
    }

    public List<Coordinates> uploadJsonFile(MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file.getInputStream(), new TypeReference<List<Coordinates>>() {});
        } catch (IOException e) {
            return null;
        }
    }
}

