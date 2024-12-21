package tim.labs.labs.service;

import tim.labs.labs.database.entity.*;
import tim.labs.labs.database.entity.enums.Role;
import tim.labs.labs.database.repository.ImportHistoryRepository;
import tim.labs.labs.database.repository.UserRepository;
import tim.labs.labs.security.jwt.service.IJwtService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ImportHistoryService {

    private ImportHistoryRepository ihRepository;
    private CoordinatesService coordinatesService;
    private LocationService locationService;
    private MovieService movieService;
    private PersonService personService;
    private FileService fileService;
    private UserRepository userRepository;

    private IJwtService jwtService;

    @Transactional
    public ImportHistory createImportHistory(String token, String entityType, MultipartFile multipartFile) {
        var ih = new ImportHistory();
        ih.setUserId(jwtService.getUserIdFromToken(token));
        ih.setStatus("SUCCESS");
        ih.setAddedObjects(0);
        ih.setTimestamp(LocalDateTime.now());

        var fileName = multipartFile.getName() + "_" + ih.getId();
        ih.setS3FileName(fileName);
        ih = ihRepository.save(ih);

        try {
            ih = createImportHistoryInternal(token, entityType, multipartFile, ih);
        } catch (ConnectException e) {
            ih.setAddedObjects(0);
            ih.setStatus("FAILED");
            ih.setReasonFailed("Error saving the file to s3");
        } catch (RuntimeException e) {
            ih.setAddedObjects(0);
            ih.setStatus("FAILED");
            ih.setReasonFailed("Internal error");
        } catch (Exception e) {
            ih.setAddedObjects(0);
            ih.setStatus("FAILED");
            ih.setReasonFailed("Unknown error");
        }
        ihRepository.save(ih);
        return ih;
    }

    @Transactional
    public ImportHistory createImportHistoryInternal(String token, String entityType, MultipartFile multipartFile, ImportHistory ih) throws Exception {
        switch (entityType) {
            case "movie":
                List<Movie> movies = movieService.uploadJsonFile(multipartFile);
                if(movies == null) {
                    ih.setStatus("FAILED");
                    ih.setReasonFailed(ImportHistory.failUpload);
                    break;
                }
                ih.setAddedObjects(movieService.insertMovies(movies));
                break;
            case "location":
                List<Location> locations = locationService.uploadJsonFile(multipartFile);
                if(locations == null) {
                    ih.setStatus("FAILED");
                    ih.setReasonFailed(ImportHistory.failUpload);
                    break;
                }
                ih.setAddedObjects(locationService.insertLocations(locations));
                break;
            case "coordinates":
                List<Coordinates> coordinates = coordinatesService.uploadJsonFile(multipartFile);
                if(coordinates == null) {
                    ih.setStatus("FAILED");
                    ih.setReasonFailed(ImportHistory.failUpload);
                    break;
                }
                ih.setAddedObjects(coordinatesService.insertCoordinates(coordinates));
                break;
            case "person":
                List<Person> persons = personService.uploadJsonFile(multipartFile);
                if(persons == null) {
                    ih.setStatus("FAILED");
                    ih.setReasonFailed(ImportHistory.failUpload);
                    break;
                }
                ih.setAddedObjects(personService.insertPersons(persons));
                break;
        }

//        if(3 > 2) {
//            throw new RuntimeException("ops");
//        }

        if(ih.getAddedObjects() == -1) {
            ih.setStatus("FAILED");
            ih.setAddedObjects(0);
            ih.setReasonFailed(ImportHistory.failConstraint);
        }

        if(!ih.getStatus().equals("FAILED")) {
            var userId = jwtService.getUserIdFromToken(token);
            var user = userRepository.findById(userId);
            if(user.isPresent()) {
                fileService.putUserFile(multipartFile, user.get(), ih.getS3FileName());
            }
        }

        return ih;
    }

    public List<ImportHistory> getAll(int pageNumber, String sortByField, int pageSize, String token) {
        var role = jwtService.getUserRoleFromToken(token);
        var userId = jwtService.getUserIdFromToken(token);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(sortByField));

        Specification<ImportHistory> spec = Specification.where(null);

        if (role != Role.Admin) {
            spec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("userId"), userId);
        }

        Page<ImportHistory> importHistoryPage = ihRepository.findAll(spec, pageRequest);
        return importHistoryPage.getContent();
    }

    public byte[] LoadFile(Long importHistoryId, Long userId) {
        var ih = ihRepository.findById(importHistoryId);
        if(ih.isEmpty() || !Objects.equals(ih.get().getUserId(), userId)) {
            return null;
        }
        var user = userRepository.findById(userId);
        if(user.isEmpty()) {
            return null;
        }
        return fileService.readFile(ih.get().getS3FileName(), user.get());
    }
}
