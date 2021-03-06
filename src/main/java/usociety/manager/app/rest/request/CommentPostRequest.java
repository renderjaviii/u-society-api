package usociety.manager.app.rest.request;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("Request to comment post.")
public class CommentPostRequest extends BaseObject {

    @NotEmpty
    @JsonProperty
    private String comment;

    public CommentPostRequest() {
        super();
    }

    public CommentPostRequest(@NotEmpty String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
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

