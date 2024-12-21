package tim.labs.labs.service;

import tim.labs.labs.database.entity.User;
import tim.labs.labs.database.repository.ImportHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;
    private final ObjectMapper objectMapper;
    private final ImportHistoryRepository importHistoryRepository;
    @Setter(onMethod_ = {@Autowired, @Lazy})
    private FileService fileService;

    @SneakyThrows
    public String createUserBucketIfNotExists(User user) {
        String bucket = "u" + user.getUsername();
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        return bucket;
    }

    @SneakyThrows
    public byte[] readFile(String filename, User user) {
        String bucket = createUserBucketIfNotExists(user);
        var object = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(filename)
                .build());
        return object.readAllBytes();
    }

    @SneakyThrows
    public void putUserFile(MultipartFile file, User user, String name) {
        String bucket = createUserBucketIfNotExists(user);
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(name)
                .contentType(file.getContentType())
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
    }
}