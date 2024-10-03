package com.example.nginep.payments.tasks;

import com.example.nginep.payments.service.PaymentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
public class CancelUnconfirmedManualPaymentTask implements Runnable {
    private final PaymentService paymentService;
    private Long paymentId;

    @Override
    public void run() {
        paymentService.cancelUnconfirmedManualPayment(paymentId);
    }
}