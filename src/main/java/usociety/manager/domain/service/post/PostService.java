package usociety.manager.domain.service.post;

import java.util.List;

import usociety.manager.app.api.PostApi;
import usociety.manager.app.rest.request.CommentPostRequest;
import usociety.manager.app.rest.request.CreatePostRequest;
import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;

public interface PostService {

    PostApi create(String username, Long groupId, CreatePostRequest request) throws GenericException;

    List<PostApi> getAllByUserAndGroup(String username, Long groupId, int page, int pageSize) throws GenericException;

    void update(PostApi post);

    void react(String username, Long postId, ReactTypeEnum value) throws GenericException;

    void comment(String username, Long postId, CommentPostRequest request) throws GenericException;

    void vote(String username, Long postId, Integer vote) throws GenericException;

}
