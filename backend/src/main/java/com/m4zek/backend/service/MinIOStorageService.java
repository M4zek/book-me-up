package com.m4zek.backend.service;

import com.m4zek.backend.exception.StorageException;
import com.m4zek.backend.minio.MinioKeyGenerator;
import com.m4zek.backend.model.ImageSize;
import io.minio.*;
import io.minio.errors.MinioException;
import jakarta.transaction.Transactional;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class MinIOStorageService {

    @Value("${minio.presigned-url-expiration}")
    private int PRESIGNED_URL_EXPIRATION;

    @Value("${minio.bucket}")
    private String BUCKET;

    private final MinioClient minioClient;

    public MinIOStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }


    public void uploadImage(MultipartFile file, String objectKey){
        try {
            objectKey = MinioKeyGenerator.changeIMGExtensionToWEBP(objectKey);

            if (!bucketExists(BUCKET))
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(BUCKET).build()
                );

            Map<ImageSize, byte[]> resizedImages = resizeImages(file);

            for (Map.Entry<ImageSize, byte[]> entry : resizedImages.entrySet()) {
                byte[] data = entry.getValue();
                ImageSize size = entry.getKey();

                // Add size of image at end of the name and before the extension
                String finalObjectKey = MinioKeyGenerator.addSizeToImageObjectKey(objectKey, size);

                try (InputStream stream = new ByteArrayInputStream(data)) {
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(BUCKET)
                                    .object(finalObjectKey)
                                    .stream(stream, (long) data.length, -1L)
                                    .contentType("image/webp")
                                    .build()
                    );
                }
            }
        } catch (MinioException | IOException e){
            throw new StorageException(e);
        }

    }

    // Remove image from MinIO. 4 version (size)
    @Transactional
    public void deleteImage(String objectKey){
        try {
            for(ImageSize size: ImageSize.types()){
                String objectKeyToRemove = MinioKeyGenerator.addSizeToImageObjectKey(objectKey, size);
                this.deleteFile(objectKeyToRemove);
            }
        } catch (MinioException e){
            throw new StorageException(e);
        }
    }

    public void deleteFile(String objectKey) throws MinioException {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(BUCKET)
                        .object(objectKey)
                        .build()
        );
    }

    public String generatePresignedURL(String objectKey) throws MinioException {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Http.Method.GET)
                        .bucket(BUCKET)
                        .object(objectKey)
                        .expiry(PRESIGNED_URL_EXPIRATION)
                        .skipValidation(false)
                        .build()
        );
    }


    public String generatePresignedURL(String objectKey, ImageSize fileSize) throws MinioException {
        if(fileSize == null) return this.generatePresignedURL(objectKey);
        objectKey = MinioKeyGenerator.changeIMGExtensionToWEBP(objectKey);
        objectKey = MinioKeyGenerator.addSizeToImageObjectKey(objectKey, fileSize);
        return this.generatePresignedURL(objectKey);
    }


    //    Private methods
    private boolean bucketExists(String bucket) throws MinioException {
        return minioClient.listBuckets().stream()
                .anyMatch(b -> b.name().equals(bucket));
    }


    /**
     * * Methods to resize image to smaller version
     * @return 4 images
     */
    private Map<ImageSize, byte[]> resizeImages(MultipartFile file) throws IOException {

        Map<ImageSize, byte[]> result = new HashMap<>();

        // ORIGINAL SIZE
        BufferedImage original = ImageIO.read(file.getInputStream());

        // SMALL
        BufferedImage small = Thumbnails.of(original)
                .size(300, 300)
                .asBufferedImage();

        // MEDIUM
        BufferedImage medium = Thumbnails.of(original)
                .size(500, 500)
                .asBufferedImage();

        // LARGE
        BufferedImage large = Thumbnails.of(original)
                .size(1000, 1000)
                .asBufferedImage();

        result.put(ImageSize.SMALL, toBytes(small));
        result.put(ImageSize.MEDIUM, toBytes(medium));
        result.put(ImageSize.LARGE, toBytes(large));
        result.put(ImageSize.ORIGINAL, toBytes(original));

        return result;
    }

    private byte[] toBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "webp", baos);
        return baos.toByteArray();
    }

}
