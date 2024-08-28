package com.example.nginep.cloudinary.controller;

import com.example.nginep.cloudinary.dto.CloudinaryUploadResponseDto;
import com.example.nginep.cloudinary.service.CloudinaryService;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import com.example.nginep.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {
    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<Response<Object>> handleImageUpload(@RequestParam("file") MultipartFile file) {
        try {
            CloudinaryUploadResponseDto result = cloudinaryService.uploadImage(file);
            return Response.successResponse("Upload Image Success", result);
        } catch (ApplicationException e) {
            return Response.failedResponse("Upload Image Failed", e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Object>> handleImageUpdate(
            @RequestParam("publicId") String publicId,
            @RequestParam("file") MultipartFile file) {
        try {
            CloudinaryUploadResponseDto result = cloudinaryService.updateImage(file, publicId);
            return Response.successResponse("Update image success", result);
        } catch (ApplicationException e) {
            return Response.failedResponse("Update image failed", e.getMessage());
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Response<Object>> handleImageDelete(@PathVariable String publicId) {
        try {
            cloudinaryService.deleteImage(publicId);
            return Response.successResponse("Image deleted successfully");
        } catch (ApplicationException e) {
            return Response.failedResponse("Failed to delete image", e.getMessage());
        }
    }
}