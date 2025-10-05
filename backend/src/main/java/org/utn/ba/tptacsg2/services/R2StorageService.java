package org.utn.ba.tptacsg2.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.utn.ba.tptacsg2.models.events.Imagen;
import org.utn.ba.tptacsg2.repositories.db.ImagenRepository;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

@Service
public class R2StorageService {

    private final S3Client s3Client;
    private final ImagenRepository imagenRepository;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.endpoint}")
    private String endpoint;

    @Value("${cloudflare.r2.public-base-url:}")
    private String publicBaseUrl;

    public R2StorageService(S3Client s3Client, ImagenRepository imagenRepository) {
        this.s3Client = s3Client;
        this.imagenRepository = imagenRepository;
    }

    public Imagen upload(MultipartFile file, Long ownerUserId) throws IOException {
        String extension = obtenerExtension(file.getOriginalFilename());
        String key = "imagenes/%s/%s/%s%s".formatted(
                Year.now(),
                String.format("%02d", LocalDate.now().getMonthValue()),
                UUID.randomUUID(),
                extension
        );

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        PutObjectResponse response = s3Client.putObject(
                request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        Imagen imagen = new Imagen(
                null,
                bucket,
                key,
                file.getContentType(),
                file.getSize(),
                file.getOriginalFilename(),
                response.eTag(),
                ownerUserId
        );

        return imagenRepository.save(imagen);
    }

    public byte[] download(String key) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        try (var response = s3Client.getObject(request)) {
            return response.readAllBytes();
        }
    }

    public String getImageUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        String base = (publicBaseUrl == null || publicBaseUrl.isBlank()) ? endpoint : publicBaseUrl;
        base = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return "%s/%s".formatted(base, key);
    }

    private String obtenerExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
