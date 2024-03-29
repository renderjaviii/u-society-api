package usociety.manager.app.rest.request;

import java.util.Set;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import usociety.manager.app.util.BaseObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserRequest extends BaseObject {

    @NotEmpty
    @JsonProperty
    private String name;

    @JsonProperty
    private String photo;

    @JsonProperty
    private Set<Long> categoryList;

    public UpdateUserRequest() {
        super();
    }

    public UpdateUserRequest(String name, String photo, Set<Long> categoryList) {
        this.name = name;
        this.photo = photo;
        this.categoryList = categoryList;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public Set<Long> getCategoryList() {
        return categoryList;
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
