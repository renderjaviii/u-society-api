package usociety.manager.domain.service.group.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.service.group.CreateGroupDelegate;
import usociety.manager.domain.service.group.GetGroupHelper;
import usociety.manager.domain.service.group.GroupMembershipHelper;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.group.UpdateGroupDelegate;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupMembershipHelper groupMembershipHelper;
    private final UpdateGroupDelegate updateGroupDelegate;
    private final CreateGroupDelegate createGroupDelegate;
    private final GetGroupHelper getGroupHelper;

    @Autowired
    public GroupServiceImpl(GroupMembershipHelper groupMembershipHelper,
                            UpdateGroupDelegate updateGroupDelegate,
                            CreateGroupDelegate createGroupDelegate,
                            GetGroupHelper getGroupHelper) {
        this.groupMembershipHelper = groupMembershipHelper;
        this.updateGroupDelegate = updateGroupDelegate;
        this.createGroupDelegate = createGroupDelegate;
        this.getGroupHelper = getGroupHelper;
    }

    @Override
    public GroupApi create(String username, CreateGroupRequest request)
            throws GenericException {
        return createGroupDelegate.execute(username, request);
    }

    @Override
    public GetGroupResponse update(String username, UpdateGroupRequest request)
            throws GenericException {
        return updateGroupDelegate.execute(username, request);
    }

    @Override
    public void join(String username, Long id) throws GenericException {
        groupMembershipHelper.join(username, id);
    }

    @Override
    public void updateMembership(String username, Long id, UserGroupApi request)
            throws GenericException {
        groupMembershipHelper.update(username, id, request);
    }

    @Override
    public Group get(Long id) throws GenericException {
        return getGroupHelper.byId(id);
    }

    @Override
    public Optional<UserGroup> getByIdAndUser(Long id, String username) throws GenericException {
        return getGroupHelper.byIdAndUser(id, username);
    }

    @Override
    public GetGroupResponse get(String username, Long id) throws GenericException {
        return getGroupHelper.byUserAndId(username, id);
    }

    @Override
    public GetGroupResponse getBySlug(String username, String slug)
            throws GenericException {
        return getGroupHelper.byUserAndSlug(username, slug);
    }

    @Override
    public List<GroupApi> getByFilters(String name, Long categoryId)
            throws GenericException {
        return getGroupHelper.byFilters(name, categoryId);
    }

    @Override
    public List<GroupApi> getAllUserGroups(String username)
            throws GenericException {
        return getGroupHelper.allUserGroups(username);
    }

    @Override
    public Optional<UserGroup> validateIfUserIsMember(String username,
                                                      Long groupId,
                                                      UserGroupStatusEnum status,
                                                      String errorCode)
            throws GenericException {
        return getGroupHelper.validateIfUserIsMember(username, groupId, status, errorCode);
    }

}
