package usociety.manager.domain.service.post;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.rest.request.CreatePostRequest;
import usociety.manager.domain.exception.GenericException;

public interface ProcessPostHelper {

    PostApi create(UserApi user, Long groupId, CreatePostRequest request) throws GenericException;

    void update(PostApi post);

}
