package com.FreeBoard.FreeBoard_Profile_Spring.service;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    private final S3Client s3Client;

    @Value("${minio.AVATAR_BUCKET_NAME}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        InputStream inputStream = file.getInputStream();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));
        return fileName;
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build());
    }

    public String updateFile(String fileName, MultipartFile file) throws IOException {
        deleteFile(fileName);
        return uploadFile(file, UUID.randomUUID() + ".jpg");
    }
}
