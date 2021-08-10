package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import usociety.manager.app.api.PaymentApi;
import usociety.manager.domain.service.payment.PaymentService;

@Tag(name = "Payment controller")
@Validated
@RestController
@RequestMapping(path = "v1/services/payments")
public class PaymentController extends AbstractController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@Valid @RequestBody PaymentApi payment) {
        paymentService.create(getUser(), payment);
        return new ResponseEntity<>(CREATED);
    }

}
