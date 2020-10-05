package usociety.manager.domain.service.user;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.UserLoginRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.exception.GenericException;

public interface UserService {

    UserApi create(CreateUserRequest request, MultipartFile photo) throws GenericException;

    void verify(String username, String email) throws GenericException;

    UserApi get(String username) throws GenericException;

    UserApi getById(Long id) throws GenericException;

    void enableAccount(String username, String otpCode) throws GenericException;

    LoginResponse login(UserLoginRequest request) throws GenericException;

    void delete(String username);

    List<UserApi> getAll();

    void changePassword(String username, String otpCode, ChangePasswordRequest request) throws GenericException;

}