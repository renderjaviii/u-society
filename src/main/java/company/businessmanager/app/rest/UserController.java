package company.businessmanager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import company.businessmanager.app.api.ApiError;
import company.businessmanager.app.api.UserApi;
import company.businessmanager.app.rest.request.CreateUserRequest;
import company.businessmanager.app.rest.request.UserLogin;
import company.businessmanager.domain.exception.GenericException;
import company.businessmanager.domain.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Validated
@RestController
@RequestMapping(path = "services/users")
public class UserController {

    @Resource(name = "tokenStore")
    TokenStore tokenStore;

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Create User.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "User created."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@Valid @RequestBody final CreateUserRequest request)
            throws GenericException {
        userService.create(request);
        return new ResponseEntity<>(CREATED);
    }

    @ApiOperation(value = "User Login.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "User logged in."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/login/",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@Valid @RequestBody final UserLogin request) {
        return new ResponseEntity<>(userService.login(request), OK);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @ApiOperation(value = "Get User.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "User data."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApi> get(@PathVariable(value = "userId") final Long userId) throws GenericException {
        return ResponseEntity.ok(userService.get(userId));
    }

}
