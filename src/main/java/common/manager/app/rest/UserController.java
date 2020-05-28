package common.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.manager.app.api.ApiError;
import common.manager.app.api.TokenApi;
import common.manager.app.rest.request.UserLoginRequest;
import common.manager.domain.exception.GenericException;
import common.manager.domain.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Validated
@RestController
@RequestMapping(path = "services/users")
public class UserController extends CommonController {

    @Resource(name = "tokenStore")
    TokenStore tokenStore;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

/*    @ApiOperation(value = "Create user.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "User created."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateUserResponse> create(@Valid @RequestBody final CreateUserRequest request)
            throws GenericException {
        return new ResponseEntity<>(userService.create(request), CREATED);
    }*/

    @ApiOperation(value = "Login.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "User logged in."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/login/",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenApi> login(@Valid @RequestBody final UserLoginRequest request)
            throws GenericException {
        return new ResponseEntity<>(userService.login(request), CREATED);
    }

    /*@ApiOperation(value = "User logged in token info.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "User logged in."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/token-info/",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Authentication> getTokenInfo(
            @ApiParam(value = "Username") @NotNull @RequestParam(value = "username") final String username)
            throws UserValidationException {
        validateUser(username);
        return new ResponseEntity<>(userService.getTokenInfo(), OK);
    }

    @ApiOperation(value = "Verify email.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "User data."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/{username}/verify-email",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> verifyEmail(@PathVariable(value = "username") final String username,
                                            @RequestParam(name = "otpCode") final String otpCode)
            throws GenericException {
        userService.enableAccount(username, otpCode);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN_PRIVILEGE')")
    @ApiOperation(value = "Get user.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "User data."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApi> get(@PathVariable(value = "username") final String username)
            throws GenericException {
        validateUser(username);
        return ResponseEntity.ok(userService.get(username));
    }*/

}
