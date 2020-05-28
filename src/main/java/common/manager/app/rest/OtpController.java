package common.manager.app.rest;

import static org.springframework.http.HttpStatus.CREATED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import common.manager.app.api.ApiError;
import common.manager.app.api.OtpApi;
import common.manager.domain.exception.GenericException;
import common.manager.domain.service.otp.OtpService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Validated
@RestController
@RequestMapping(path = "services/otp")
public class OtpController {

    private final OtpService otpService;

    @Autowired
    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @ApiOperation(value = "Create user OTP.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "OTP created."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/")
    public ResponseEntity<OtpApi> create(@RequestParam(name = "username") final String username) {
        return new ResponseEntity<>(otpService.create(username), CREATED);
    }

    @ApiOperation(value = "Validate user OTP.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "OTP validated."),
            @ApiResponse(code = 400, message = "Input data error.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Internal server error.", response = ApiError.class) })
    @GetMapping(path = "/validate")
    public ResponseEntity<Void> validate(@RequestParam(name = "username") final String username,
                                         @RequestParam(name = "otpCode") final String otpCode)
            throws GenericException {
        otpService.validate(username, otpCode);
        return ResponseEntity.ok().build();
    }

}