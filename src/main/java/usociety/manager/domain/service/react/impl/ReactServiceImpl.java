package usociety.manager.domain.service.react.impl;

import static usociety.manager.domain.enums.UserGroupStatusEnum.ACTIVE;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.PostTypeEnum;
import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Post;
import usociety.manager.domain.model.React;
import usociety.manager.domain.repository.ReactRepository;
import usociety.manager.domain.service.common.impl.AbstractDelegateImpl;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.post.dto.PostAdditionalData;
import usociety.manager.domain.service.react.ReactService;

@Service
public class ReactServiceImpl extends AbstractDelegateImpl implements ReactService {

    private static final String REACTING_IN_POST_ERROR_CODE = "ERROR_REACTING_IN_POST";
    private static final String REACTING_POST_ERROR_CODE = "ERROR_REACTING_TO_POST";

    private final ReactRepository reactRepository;
    private final GroupService groupService;

    @Autowired
    public ReactServiceImpl(ReactRepository reactRepository,
                            GroupService groupService) {
        this.reactRepository = reactRepository;
        this.groupService = groupService;
    }

    @Override
    public void create(String username, Post post, ReactTypeEnum value) throws GenericException {
        UserApi user = getUser(username);

        groupService.validateIfUserIsMember(username, post.getGroup().getId(), ACTIVE, REACTING_POST_ERROR_CODE);
        validatePostType(post);

        Optional<React> optionalReact = reactRepository.findAllByPostIdAndUserId(post.getId(), user.getId());
        if (optionalReact.isPresent()) {
            React savedReact = optionalReact.get();
            savedReact.setValue(value.getCode());
            reactRepository.save(savedReact);
        } else {
            reactRepository.save(React.newBuilder()
                    .value(value.getCode())
                    .userId(user.getId())
                    .post(post)
                    .build());
        }
    }

    private void validatePostType(Post post) throws GenericException {
        try {
            PostAdditionalData postAdditionalData = objectMapper.readValue(post.getContent(), PostAdditionalData.class);
            if (PostTypeEnum.SURVEY == postAdditionalData.getType()) {
                throw new GenericException("No es posible reaccionar a encuestas.", REACTING_IN_POST_ERROR_CODE);
            }
        } catch (JsonProcessingException e) {
            throw new GenericException("Información de post corrupta.", REACTING_IN_POST_ERROR_CODE);
        }
    }

}
