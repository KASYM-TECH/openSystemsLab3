package tim.labs.labs.service;

import tim.labs.labs.database.entity.Location;
import tim.labs.labs.database.entity.enums.Role;
import tim.labs.labs.database.repository.LocationRepository;
import tim.labs.labs.database.repository.PersonRepository;
import tim.labs.labs.database.repository.specifications.LocationSpecifications;
import tim.labs.labs.exception.ForbiddenException;
import tim.labs.labs.security.jwt.service.IJwtService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LocationService {
    private LocationRepository locationRepository;
    private PersonRepository personRepository;
    private IJwtService jwtService;

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location getLocationById(Long id) throws Exception {
        return locationRepository.findById(id).orElseThrow(() -> new Exception("Location not found"));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Location updateLocation(Long id, Location updatedLocation, String token) throws Exception {
        var location = locationRepository.findById(id).orElseThrow();
        var userRole = jwtService.getUserRoleFromToken(token);
        var userId = jwtService.getUserIdFromToken(token);

        if(location.getCreatorId() != userId) {
            if(userRole != Role.Admin) {
                throw new ForbiddenException();
            }
            if(!location.getAllowAdminEdit()){
                throw new ForbiddenException();
            }
        }

        location.setX(updatedLocation.getX());
        location.setY(updatedLocation.getY());
        location.setZ(updatedLocation.getZ());
        location.setName(updatedLocation.getName());

        return locationRepository.save(location);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void replaceWith(Long id, Long replaceWithId) {
        if(Objects.equals(id, replaceWithId)) {
            return ;
        }
        personRepository.replaceLocation(id, replaceWithId);
        locationRepository.deleteById(id);
    }

    public List<Location> getAll(int pageNumber, String sortByField, int pageSize, String name) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortByField));

        Specification<Location> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(LocationSpecifications.hasName(name));
        }

        Page<Location> coordinatesPage = locationRepository.findAll(spec, pageRequest);
        return coordinatesPage.getContent();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public int insertLocations(List<Location> locationsList) {
        var count = 0;
        for (Location location : locationsList) {
            if (!location.Validate()) {
                return -1;
            }
            count++;
        }
        locationRepository.saveAll(locationsList);
        return count;
    }

    public List<Location> uploadJsonFile(MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file.getInputStream(), new TypeReference<List<Location>>() {});
        } catch (IOException e) {
            return null;
        }
    }
}

