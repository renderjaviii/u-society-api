package usociety.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import usociety.manager.app.api.ApiError;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.UserLoginRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.exception.UserValidationException;
import usociety.manager.domain.service.user.UserService;

@Validated
@RestController
@RequestMapping(path = "services/users")
public class UserController extends CommonController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Create.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "User created."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApi> create(@Valid @RequestPart("user") CreateUserRequest request,
                                          @RequestPart("photo") MultipartFile photo)
            throws GenericException {
        return new ResponseEntity<>(userService.create(request, photo), CREATED);
    }

    @ApiOperation(value = "Verify user data and send otp.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "User verified and otp sent."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/{username}/verify")
    public ResponseEntity<Void> verify(@PathVariable(value = "username") final String username,
                                       @Email @RequestParam(name = "email") final String email)
            throws GenericException {
        userService.verify(username, email);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Get.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "User data."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserApi> get(@PathVariable(value = "username") final String username)
            throws GenericException {
        validateUser(username);
        return ResponseEntity.ok(userService.get(username));
    }

    @ApiOperation(value = "Login.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Token data."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PostMapping(path = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final UserLoginRequest request)
            throws GenericException {
        return new ResponseEntity<>(userService.login(request), OK);
    }

    @ApiOperation(value = "Delete.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "User deleted."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @DeleteMapping(path = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable(value = "username") final String username)
            throws UserValidationException {
        validateUser(username);
        userService.delete(username);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Change password.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Password changed."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 401, message = "Unauthorized.", response = ApiError.class),
            @ApiResponse(code = 409, message = "Internal validation error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @PatchMapping(path = "/{username}/changePassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserApi>> changePassword(@PathVariable(value = "username") final String username,
                                                        @RequestParam(name = "otpCode") final String otpCode,
                                                        @Valid @RequestBody ChangePasswordRequest request)
            throws GenericException {
        userService.changePassword(validateUser(username), otpCode, request);
        return ResponseEntity.ok().build();
    }

}