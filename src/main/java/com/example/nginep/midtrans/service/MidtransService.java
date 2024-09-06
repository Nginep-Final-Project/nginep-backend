package com.example.nginep.midtrans.service;

import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.service.MidtransCoreApi;
import com.example.nginep.exceptions.applicationException.ApplicationException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MidtransService {

    private final MidtransCoreApi coreApi;

    public MidtransService(
            @Value("${MIDTRANS_SERVER_KEY}") String serverKey,
            @Value("${MIDTRANS_CLIENT_KEY}") String clientKey,
            @Value("${MIDTRANS_IS_PRODUCTION}") boolean isProduction
    ) {
        Config config = Config.builder()
                .setIsProduction(isProduction)
                .setServerKey(serverKey)
                .setClientKey(clientKey)
                .build();

        this.coreApi = new ConfigFactory(config).getCoreApi();
    }

    public JSONObject createBankTransferCharge(String orderId, long amount, String bank) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("transaction_details", Map.of(
                    "order_id", orderId,
                    "gross_amount", amount
            ));

            Instant now = Instant.now();
            String formattedNow = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")
                    .withZone(ZoneOffset.UTC)
                    .format(now);

            params.put("custom_expiry", Map.of(
                    "order_time", formattedNow,
                    "expiry_duration", 60,
                    "unit", "minute"
            ));

            switch (bank.toLowerCase()) {
                case "bca":
                case "bni":
                case "bri":
                    params.put("payment_type", "bank_transfer");
                    params.put("bank_transfer", Map.of("bank", bank.toLowerCase()));
                    break;
                case "permata":
                    params.put("payment_type", "permata");
                    break;
                case "mandiri":
                    params.put("payment_type", "echannel");
                    params.put("echannel", Map.of(
                            "bill_info1", "Payment for order " + orderId,
                            "bill_info2", "Midtrans"
                    ));
                    break;
                default:
                    throw new ApplicationException("Unsupported bank: " + bank);
            }

            return coreApi.chargeTransaction(params);
        } catch (Exception e) {
            throw new ApplicationException("Failed to create Midtrans charge: " + e.getMessage());
        }
    }

    public JSONObject getTransactionStatus(String orderId) {
        try {
            return coreApi.checkTransaction(orderId);
        } catch (Exception e) {
            throw new ApplicationException("Failed to get transaction status: " + e.getMessage());
        }
    }

}