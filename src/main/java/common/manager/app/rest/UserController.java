package common.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import common.manager.app.api.ErrorApi;
import common.manager.app.api.UserApi;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.domain.exception.GenericException;
import common.manager.domain.service.user.UserService;
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

    @ApiOperation(value = "Create user.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "User created."),
            @ApiResponse(code = 400, message = "Input data error.", response = ErrorApi.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ErrorApi.class) })
    @PostMapping(path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@Valid @RequestBody final CreateUserRequest request)
            throws GenericException {
        userService.create(request);
        return new ResponseEntity<>(CREATED);
    }

    @ApiOperation(value = "Token info user logged in.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "User logged in."),
            @ApiResponse(code = 400, message = "Input data error.", response = ErrorApi.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ErrorApi.class) })
    @GetMapping(path = "/token-info/",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Authentication> getTokenInfo() {
        return new ResponseEntity<>(userService.getTokenInfo(), OK);
    }

    @PreAuthorize("hasAuthority('ADMIN_PRIVILEGE')")
    @ApiOperation(value = "Get user.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "User data."),
            @ApiResponse(code = 400, message = "Input data error.", response = ErrorApi.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ErrorApi.class) })
    @GetMapping(path = "/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApi> get(@Valid @NotNull @PathVariable(value = "userId") final Long userId)
            throws GenericException {
        return ResponseEntity.ok(userService.get(userId));
    }

}
