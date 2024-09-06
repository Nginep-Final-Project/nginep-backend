package com.example.nginep.payments.controller;

import com.example.nginep.orchestrator.BookingPaymentOrchestrator;
import com.example.nginep.payments.dto.UploadProofOfPaymentDto;
import com.example.nginep.payments.entity.Payment;
import com.example.nginep.payments.service.PaymentService;
import com.example.nginep.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingPaymentOrchestrator bookingPaymentOrchestrator;

    @PostMapping(value = "/upload-proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<Payment>> uploadProofOfPayment(
            @ModelAttribute @Valid UploadProofOfPaymentDto uploadProofOfPaymentDTO) {
        Payment updatedPayment = paymentService.uploadProofOfPayment(uploadProofOfPaymentDTO);
        return Response.successResponse("Proof of payment uploaded successfully", updatedPayment);
    }

    @PostMapping("/{paymentId}/confirm-manual")
    public ResponseEntity<Response<Payment>> confirmManualPayment(@PathVariable Long paymentId) {
        Payment confirmedPayment = paymentService.confirmManualPayment(paymentId);
        return Response.successResponse("Manual payment confirmed successfully", confirmedPayment);
    }

    @PostMapping("/{paymentId}/reject")
    public ResponseEntity<Response<Object>> rejectPayment(@PathVariable Long paymentId) {
        bookingPaymentOrchestrator.rejectPayment(paymentId);
        return Response.successResponse("Payment rejected successfully");
    }

    @GetMapping("/{paymentId}/midtrans-details")
    public ResponseEntity<Response<Map<String, Object>>> getMidtransDetails(@PathVariable String paymentId) {
        Map<String, Object> paymentDetails = paymentService.getMidtransDetails(paymentId);
        return Response.successResponse("Payment details retrieved successfully", paymentDetails);
    }
}