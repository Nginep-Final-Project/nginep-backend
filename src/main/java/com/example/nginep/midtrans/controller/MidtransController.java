package com.example.nginep.midtrans.controller;

import com.example.nginep.midtrans.service.MidtransNotificationHandler;
import com.example.nginep.midtrans.service.MidtransService;
import com.example.nginep.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/midtrans")
@RequiredArgsConstructor
public class MidtransController {

    private final MidtransService midtransService;
    private final MidtransNotificationHandler midtransNotificationHandler;

    @PostMapping("/notifications")
    public ResponseEntity<Response<Object>> handleNotification(@RequestBody String notificationPayload) {
        midtransNotificationHandler.handleNotification(notificationPayload);
        return Response.successResponse("Notification processed successfully");
    }
}