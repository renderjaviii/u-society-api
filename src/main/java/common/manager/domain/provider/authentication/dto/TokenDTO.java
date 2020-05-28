package common.manager.domain.provider.authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("Token DTO")
public class TokenDTO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("jti")
    private String jti;

    public TokenDTO() {
        super();
    }

    private TokenDTO(Builder builder) {
        accessToken = builder.accessToken;
        tokenType = builder.tokenType;
        refreshToken = builder.refreshToken;
        expiresIn = builder.expiresIn;
        scope = builder.scope;
        jti = builder.jti;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public String getJti() {
        return jti;
    }

    public static final class Builder {

        private String accessToken;
        private String tokenType;
        private String refreshToken;
        private String expiresIn;
        private String scope;
        private String jti;

        private Builder() {
            super();
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder expiresIn(String expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder jti(String jti) {
            this.jti = jti;
            return this;
        }

        public TokenDTO build() {
            return new TokenDTO(this);
        }

    }

}
