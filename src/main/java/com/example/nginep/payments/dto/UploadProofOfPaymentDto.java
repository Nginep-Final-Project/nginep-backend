package com.example.nginep.payments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadProofOfPaymentDto {
    @NotNull(message = "Proof of payment file is required")
    private MultipartFile proofOfPayment;

    @NotNull(message = "Payment ID is required")
    private Long paymentId;
}