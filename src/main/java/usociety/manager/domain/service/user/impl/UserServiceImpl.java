package usociety.manager.domain.service.user.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import usociety.manager.app.api.OtpApi;
import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.app.rest.request.UserLoginRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.exception.WebException;
import usociety.manager.domain.model.UserCategory;
import usociety.manager.domain.provider.authentication.AuthenticationConnector;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.repository.CategoryRepository;
import usociety.manager.domain.repository.UserCategoryRepository;
import usociety.manager.domain.service.aws.s3.S3Service;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.otp.OtpService;
import usociety.manager.domain.service.user.UserService;
import usociety.manager.domain.util.Constant;

@Service
public class UserServiceImpl implements UserService {

    private final AuthenticationConnector authenticationConnector;
    private final UserCategoryRepository userCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UserConnector userConnector;
    private final MailService mailService;
    private final OtpService otpService;
    private final S3Service s3Service;

    @Value("${config.user.validate-otp:0}")
    private boolean validateOtp;

    @Autowired
    public UserServiceImpl(AuthenticationConnector authenticationConnector,
                           UserConnector userConnector,
                           MailService mailService,
                           OtpService otpService,
                           S3Service s3Service,
                           UserCategoryRepository userCategoryRepository,
                           CategoryRepository categoryRepository) {
        this.authenticationConnector = authenticationConnector;
        this.userConnector = userConnector;
        this.mailService = mailService;
        this.otpService = otpService;
        this.s3Service = s3Service;
        this.userCategoryRepository = userCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public UserApi create(CreateUserRequest request, MultipartFile photo) throws GenericException {
        if (validateOtp) {
            otpService.validate(request.getUsername(), request.getOtpCode());
        }
        validateUser(request.getUsername(), request.getEmail());

        String photoUrl = s3Service.upload(photo);
        request.setPhoto(photoUrl);

        UserDTO user;
        try {
            user = userConnector.create(request);
        } catch (Exception ex) {
            s3Service.delete(photoUrl);
            throw new GenericException("El usuario no pudo ser creado", "USER_NOT_CREATED_ERROR");
        }

        String content = String.format("¡Hola %s!, bienvenido a U Society.", StringUtils.capitalize(request.getName()));
        mailService.send(request.getEmail(), content);
        return Converter.user(user);
    }

    @Override
    public void verify(String username, String email) throws GenericException {
        validateUser(username, email);
        OtpApi userOtp = otpService.create(username, email);
        mailService.sendOtp(email, userOtp.getOtpCode());
    }

    @Override
    public UserApi get(String username) {
        return Converter.user(userConnector.get(username));
    }

    @Override
    public UserApi getById(Long id) {
        return Converter.user(userConnector.get(id, null, null));
    }

    @Override
    public void enableAccount(String username, String otpCode) throws GenericException {
        otpService.validate(username, otpCode);
        userConnector.enableAccount(username);
    }

    @Override
    public LoginResponse login(UserLoginRequest request) {
        UserDTO user = userConnector.get(request.getUsername());
        TokenApi token = authenticationConnector.login(request);

        UserApi userApi = Converter.user(user);
        userApi.setCategoryList(userCategoryRepository.findAllByUserId(userApi.getId())
                .stream()
                .map(userCategory -> Converter.category(userCategory.getCategory()))
                .collect(Collectors.toList()));

        return new LoginResponse(userApi, token);
    }

    @Override
    public void delete(String username) {
        userConnector.delete(username);
    }

    @Override
    public List<UserApi> getAll() {
        return userConnector.getAll()
                .stream()
                .map(Converter::user)
                .collect(Collectors.toList());
    }

    @Override
    public void changePassword(String username, String otpCode, ChangePasswordRequest request) throws GenericException {
        otpService.validate(username, otpCode);
        userConnector.changePassword(username, request);
    }

    @Override
    public void update(String username, UpdateUserRequest request, MultipartFile photo) throws GenericException {
        String photoUrl = null;
        if (Objects.nonNull(photo) && !photo.isEmpty()) {
            photoUrl = s3Service.upload(photo);
        }

        UserApi userApi = get(username);
        userApi.setName(StringUtils.defaultString(request.getName(), request.getName()));
        userApi.setPhoto(StringUtils.defaultString(photoUrl, userApi.getPhoto()));

        List<UserCategory> userCategoryList = userCategoryRepository.findAllByUserId(userApi.getId());
        userCategoryRepository.deleteInBatch(userCategoryList);

        request.getCategoryList()
                .forEach(categoryApi -> userCategoryRepository.save(UserCategory.newBuilder()
                        .category(categoryRepository.getOne(categoryApi.getId()))
                        .userId(userApi.getId())
                        .build()));
    }

    private void validateUser(String username, String email) throws GenericException {
        try {
            UserDTO user = userConnector.get(null, username, email);
            if (user != null) {
                throw new GenericException("Usuario ya registrado, por favor verifique el alias y el correo.",
                        "USER_ALREADY_EXISTS");
            }
        } catch (WebException ex) {
            if (!Constant.USER_NOT_FOUND.equals(ex.getErrorCode())) {
                throw new GenericException(ex.getMessage());
            }
        }
    }

}
