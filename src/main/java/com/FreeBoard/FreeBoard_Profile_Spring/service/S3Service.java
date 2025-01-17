package com.FreeBoard.FreeBoard_Profile_Spring.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    private String bucketName = "avatars";

    /**
     * Метод для загрузки файла в MinIO.
     * @param file файл, который необходимо загрузить.
     * @param fileName имя файла в MinIO.
     * @return URL файла на MinIO.
     */
    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        // Преобразуем MultipartFile в InputStream
        try (InputStream inputStream = file.getInputStream()) {
            // Создаем запрос на загрузку файла
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName) // Указываем имя файла в бакете
                    .build();

            // Загружаем файл в MinIO
            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, file.getSize()));
            return "http://localhost:9000/" + bucketName + "/" + fileName; // URL для доступа к файлу
        } catch (S3Exception e) {
            throw new RuntimeException("Error uploading file to MinIO", e);
        }
    }
}
