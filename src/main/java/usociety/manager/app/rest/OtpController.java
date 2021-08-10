package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import usociety.manager.app.api.OtpApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.otp.OtpService;

//TODO: Move it to a independent microservice

@Tag(name = "OTP controller")
@Validated
@RestController
@RequestMapping(path = "v1/services/otps")
public class OtpController extends AbstractController {

    private final OtpService otpService;

    @Autowired
    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PreAuthorize("hasAuthority('ADMIN_PRIVILEGE')")
    @Operation(summary = "Create and send")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OtpApi> create(@RequestParam(name = "email") @Email @NotEmpty final String email) {
        return new ResponseEntity<>(otpService.create(email), CREATED);
    }

    @Operation(summary = "Validate")
    @PostMapping(path = "/validate")
    public ResponseEntity<Void> validate(
            @Email @NotEmpty @RequestParam(name = "email") final String email
    ) throws GenericException {
        otpService.validate(email, getHeader("otpCode"));
        return ResponseEntity.noContent().build();
    }

}
