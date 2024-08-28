package com.example.nginep.cloudinary.service;

import com.example.nginep.cloudinary.dto.CloudinaryUploadResponseDto;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    CloudinaryUploadResponseDto uploadImage(MultipartFile file) throws ApplicationException;
    CloudinaryUploadResponseDto updateImage(MultipartFile file, String publicId) throws ApplicationException;
    void deleteImage(String publicId) throws ApplicationException;
}
