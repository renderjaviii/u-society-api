package usociety.manager.domain.provider.user.impl;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static usociety.manager.domain.provider.web.RestClientFactoryBuilder.newBuilder;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.domain.provider.user.UserConnector;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.provider.web.RestClient;
import usociety.manager.domain.provider.web.RestClientFactory;

@Component
public class UserConnectorImpl implements UserConnector {

    private final RestClient restClient;
    private final String path;

    @Autowired
    public UserConnectorImpl(@Value("${server.ssl.key-store-password}") String keyStorePassword,
                             @Value("${web.connection.time-out:5000}") int connectionTimeOut,
                             @Value("${server.ssl.key-store-type}") String keyStoreType,
                             @Value("${web.authentication.users-path}") String path,
                             @Value("${web.read.time-out:30000}") int readTimeOut,
                             @Value("${web.authentication.url}") String baseURL,
                             @Value("${server.ssl.key-store}") Resource keyStore,
                             RestClientFactory restClientFactory) {
        this.restClient = restClientFactory.create(newBuilder()
                .baseURL(baseURL)
                .readTimeOut(readTimeOut)
                .connectionTimeOut(connectionTimeOut)
                .keyStore(keyStore)
                .keyStoreType(keyStoreType)
                .keyStorePassword(keyStorePassword)
                .build());
        this.path = path;
    }

    @PostConstruct
    private void init() {
        restClient.setUp();
    }

    @Override
    public UserDTO create(CreateUserRequest body) {
        return restClient.post(uriBuilder -> uriBuilder.path(path).build(), body, UserDTO.class);
    }

    @Override
    public UserDTO update(UserDTO body) {
        return restClient.patch(uriBuilder -> uriBuilder.path(path).build(), body, UserDTO.class);
    }

    @Override
    public UserDTO get(String username) {
        return restClient.get(uriBuilder -> uriBuilder.path(path).pathSegment(username).build(), UserDTO.class);
    }

    @Override
    public UserDTO get(Long id, String username, String email) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("id", Objects.isNull(id) ? EMPTY : id.toString());
        queryParams.add("username", username);
        queryParams.add("email", email);

        return restClient.get(uriBuilder -> uriBuilder.path(path).queryParams(queryParams).build(), UserDTO.class);
    }

    @Override
    public void enableAccount(String username) {
        restClient.post(uriBuilder -> uriBuilder
                        .path(path)
                        .pathSegment("{username}")
                        .pathSegment("verify-email")
                        .build(username),
                Void.class);
    }

    @Override
    public void delete(String username) {
        restClient.delete(uriBuilder -> uriBuilder.path(path).pathSegment("{username}").build(username), Void.class);
    }

    @Override
    public List<UserDTO> getAll() {
        return restClient.get(uriBuilder -> uriBuilder
                        .path(path)
                        .pathSegment("all")
                        .build(),
                new ParameterizedTypeReference<List<UserDTO>>() {
                });
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest body) {
        restClient.patch(uriBuilder -> uriBuilder
                        .path(path)
                        .pathSegment("{username}")
                        .pathSegment("change-password")
                        .build(username),
                body,
                Void.class);
    }

}
