package tim.labs.labs.controller;

import tim.labs.labs.database.entity.ImportHistory;
import tim.labs.labs.database.repository.ImportHistoryRepository;
import tim.labs.labs.security.jwt.service.IJwtService;
import tim.labs.labs.service.ImportHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/import-history")
@AllArgsConstructor
public class ImportHistoryController {
    private ImportHistoryService importHistoryService;
    private WebSocketController webSocketController;
    private ImportHistoryRepository importHistoryRepository;
    private IJwtService jwtService;

    @GetMapping()
    public ResponseEntity<List<ImportHistory>> getAll(@RequestParam(value = "page", defaultValue = "0") int pageNumber,
                                                      @RequestParam(value = "sortBy", defaultValue = "id") String sortField,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpServletRequest request
                                                 ) {
        List<ImportHistory> ImportHistory = importHistoryService.getAll(pageNumber, sortField, pageSize, request.getHeader("Authorization"));
        return ResponseEntity.ok(ImportHistory);
    }

    @PostMapping()
    public ResponseEntity<ImportHistory> create(@RequestParam("file") MultipartFile file, @RequestParam(value = "entity", defaultValue = "0") String entityType, HttpServletRequest request) {
        var ih = new ImportHistory();
        ImportHistory ret = new ImportHistory();
        try {
            importHistoryService.createImportHistory(request.getHeader("Authorization"), entityType, file, ret);
        } catch (Exception e) {
        }
        var userId = jwtService.getUserIdFromToken(request.getHeader("Authorization"));
        ih.setStatus(ret.getStatus());
        ih.setReasonFailed(ret.getReasonFailed());
        ih.setTimestamp(LocalDateTime.now());
        ih.setAddedObjects(ret.getAddedObjects());
        ih.setUserId(userId);

        importHistoryRepository.save(ih);
        var created = ResponseEntity.ok(ih);
        webSocketController.update("");
        if(Objects.equals(ih.getStatus(), "FAILED")) {
            return ResponseEntity.badRequest().build();
        }
        return created;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id, HttpServletRequest request) {
        try {
            var userId = jwtService.getUserIdFromToken(request.getHeader("Authorization"));
            return ResponseEntity.ok(importHistoryService.LoadFile(id, userId));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}