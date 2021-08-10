package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.exception.UserValidationException;
import usociety.manager.domain.service.user.UserService;

@Tag(name = "User controller")
@Validated
@RestController
@RequestMapping(path = "v1/services/users")
public class UserController extends AbstractController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> create(@Valid @RequestBody CreateUserRequest request)
            throws GenericException, MessagingException {
        return new ResponseEntity<>(userService.create(request), CREATED);
    }

    @Operation(summary = "Verify data and send OTP")
    @PostMapping(path = "/{email}/verify")
    public ResponseEntity<Void> verify(
            @Email @NotEmpty @PathVariable(name = "email") final String email
    ) throws GenericException {
        userService.verify(email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enable user account through OTP")
    @PostMapping(path = "/{email}/enable-account")
    public ResponseEntity<Void> enableAccount(
            @Email @NotEmpty @PathVariable(name = "email") final String email,
            @NotEmpty @RequestParam(name = "otpCode") final String otpCode
    ) throws GenericException {
        userService.enableAccount(email, otpCode);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get")
    @GetMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApi> get(@PathVariable(value = "username") final String username)
            throws GenericException {
        return ResponseEntity.ok(userService.get(username));
    }

    @Operation(summary = "Update")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApi> update(@Valid @RequestBody UpdateUserRequest request)
            throws GenericException {
        return ResponseEntity.ok(userService.update(getUser(), request));
    }

    @Operation(summary = "Login")
    @PostMapping(path = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final LoginRequest request)
            throws GenericException {
        return new ResponseEntity<>(userService.login(request), OK);
    }

    @PreAuthorize("hasAuthority('ADMIN_PRIVILEGE')")
    @Operation(summary = "Delete")
    @DeleteMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable(value = "username") final String username)
            throws UserValidationException {
        validateUser(username);
        userService.delete(username);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change password")
    @PatchMapping(path = "/{username}/change-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(
            @PathVariable(value = "username") final String username,
            @RequestParam(name = "otpCode") final String otpCode,
            @Valid @RequestBody ChangePasswordRequest request
    ) throws GenericException {
        userService.changePassword(validateUser(username), otpCode, request);
        return ResponseEntity.noContent().build();
    }

}
