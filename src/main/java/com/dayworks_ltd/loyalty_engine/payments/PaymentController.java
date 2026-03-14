package com.dayworks_ltd.loyalty_engine.payments;

import com.dayworks_ltd.loyalty_engine.payments.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments endpoint", description = "handle payments through m-pesa")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/callback")
    @Operation(summary = "callback method", description = "invoked in response by the m-pesa api when a payment is made")
    public ResponseEntity<?> callback(@RequestBody PaymentNotification paymentNotification){
        var response = paymentService.recordPayment(paymentNotification);
        return new ResponseEntity<>(response , HttpStatus.OK);
    }

    @PostMapping("/intiate-payment")
    @Operation(summary = "initiate stk push", description = "initiate stk push of specified amount to specified number")
    public ResponseEntity<?> intiatePayment(@RequestBody IntiatePaymentRequest intiatePaymentRequest){
        IntiatePaymentResponse intiatePaymentResponse = paymentService.intiatePayment(intiatePaymentRequest);
        return new ResponseEntity<>(intiatePaymentResponse, HttpStatus.OK );
    }

    @PostMapping("/confirm-payment")
    @Operation( summary = "confirm initiated payment", description = "confirm the status of the specified payment")
    public ResponseEntity<?> confirmPayment(@RequestBody ConfirmPaymentRequest confirmPaymentRequest){
        ConfirmPaymentResponse confirmPaymentResponse = paymentService.confirmPayment(confirmPaymentRequest);
        return new ResponseEntity<>(confirmPaymentResponse, HttpStatus.OK );
    }

}
