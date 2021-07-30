package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import usociety.manager.app.api.ApiError;
import usociety.manager.app.api.OtpApi;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.service.otp.OtpService;

//TODO: Move it to a independent microservice
@CrossOrigin(origins = "*", maxAge = 86400)
@Validated
@RestController
@RequestMapping(path = "services/otps")
public class OtpController {

    private final OtpService otpService;

    @Autowired
    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @ApiOperation(value = "Create OTP.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "OTP created."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 406, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OtpApi> create(@RequestParam(name = "email") @Email @NotEmpty final String email) {
        return new ResponseEntity<>(otpService.create(email), CREATED);
    }

    @ApiOperation(value = "Validate OTP.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OTP validated."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 406, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PatchMapping(path = "/validate")
    public ResponseEntity<Void> validate(
            @Email @NotEmpty @RequestParam(name = "email") final String email,
            @NotEmpty @RequestParam(name = "otpCode") final String otpCode
    ) throws GenericException {
        otpService.validate(email, otpCode);
        return ResponseEntity.ok().build();
    }

}
