package com.example.nginep.cloudinary.dto;

import lombok.Data;

@Data
public class CloudinaryUploadResponseDto {
    private String publicId;
    private String url;
    private String format;
}
