package com.aunghein.SpringTemplate.service.minio;

import com.aunghein.SpringTemplate.utils.FileStorageManager;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    private FileStorageManager fileStorageManager;

    public MinioService(MinioClient minioClient,
                        @Value("${minio.bucket-name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    // Upload file
    public String uploadFile(MultipartFile file) throws Exception {
        // Check bucket existence, create if missing
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        String objectName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        }

        // Return URL or object name
        return fileStorageManager.getFileStorage() + objectName;
    }


    // Delete file
    public void deleteFile(String url) throws Exception {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!bucketExists) {
            throw new IllegalArgumentException("Bucket " + bucketName + " does not exist");
        }
        String objectName = FileStorageManager.extractUrl(url);
        StatObjectResponse stat = null;
        try {
            stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(objectName).build()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("File " + objectName + " does not exist in bucket " + bucketName);
        }

        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }



    // Optional: Generate a presigned URL to download
    public String getPresignedUrl(String objectName, int expiryInMinutes) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(expiryInMinutes, TimeUnit.MINUTES)
                .build());
    }
}