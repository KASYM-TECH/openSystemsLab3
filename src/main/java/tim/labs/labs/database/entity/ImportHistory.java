package tim.labs.labs.database.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status; // SUCCESS or FAILED

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Integer addedObjects;

    @Column()
    private String reasonFailed;

    @Column()
    private String s3FileName;

    public ImportHistory() {
    }

    public static String failUpload = "failed to parse the file";

    public static String failConstraint = "failed constrained";

    public ImportHistory(String status, Long userId, Integer addedObjects, String reasonFailed, String s3FileName) {
        this.status = status;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
        this.reasonFailed = reasonFailed;
        this.s3FileName = s3FileName;
    }
}