package usociety.manager.app.api;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.UserGroupStatusEnum;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserGroupApi extends BaseObject {

    @NotNull
    @JsonProperty
    private UserApi member;

    @JsonProperty
    private UserGroupStatusEnum status;

    public UserGroupApi() {
        super();
    }

    private UserGroupApi(Builder builder) {
        member = builder.member;
        status = builder.status;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public UserApi getMember() {
        return member;
    }

    public UserGroupStatusEnum getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private UserApi member;
        private UserGroupStatusEnum status;

        private Builder() {
            super();
        }

        public Builder member(UserApi member) {
            this.member = member;
            return this;
        }

        public Builder status(UserGroupStatusEnum status) {
            this.status = status;
            return this;
        }

        public UserGroupApi build() {
            return new UserGroupApi(this);
        }

    }

}
