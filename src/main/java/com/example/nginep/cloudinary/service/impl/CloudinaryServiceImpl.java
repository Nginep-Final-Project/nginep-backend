package com.example.nginep.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.nginep.cloudinary.dto.CloudinaryUploadResponseDto;
import com.example.nginep.cloudinary.service.CloudinaryService;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(@Value("${CLOUDINARY_CLOUD_NAME}") String cloudName,
                                 @Value("${CLOUDINARY_API_KEY}") String apiKey,
                                 @Value("${CLOUDINARY_API_SECRET}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Override
    public CloudinaryUploadResponseDto uploadImage(MultipartFile file) throws ApplicationException {
        if (file.isEmpty()) {
            throw new ApplicationException("File is empty");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return createResultDto(uploadResult);
        } catch (IOException e) {
            throw new ApplicationException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public CloudinaryUploadResponseDto updateImage(MultipartFile file, String publicId) throws ApplicationException {
        if (file.isEmpty()) {
            throw new ApplicationException("File is empty");
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return createResultDto(uploadResult);
        } catch (IOException e) {
            throw new ApplicationException("Failed to update image: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String publicId) throws ApplicationException {
        if (publicId == null || publicId.isEmpty()) {
            throw new ApplicationException("Public ID is required");
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new ApplicationException("Failed to delete image with publicId: " + publicId);
        }
    }

    private CloudinaryUploadResponseDto createResultDto(Map<?, ?> uploadResult) {
        CloudinaryUploadResponseDto resultDto = new CloudinaryUploadResponseDto();
        resultDto.setPublicId((String) uploadResult.get("public_id"));
        resultDto.setUrl((String) uploadResult.get("url"));
        resultDto.setFormat((String) uploadResult.get("format"));
        return resultDto;
    }

}