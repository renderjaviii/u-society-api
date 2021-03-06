package usociety.manager.app.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.util.BaseObject;

@ApiModel("Login response info.")
public class LoginResponse extends BaseObject {

    @JsonProperty(value = "user")
    private UserApi user;
    @JsonProperty(value = "token")
    private TokenApi token;

    public LoginResponse() {
        super();
    }

    public LoginResponse(UserApi user, TokenApi token) {
        this.user = user;
        this.token = token;
    }

    public UserApi getUser() {
        return user;
    }

    public TokenApi getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
