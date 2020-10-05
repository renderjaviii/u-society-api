package usociety.manager.domain.service.group.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserGroupApi;
import usociety.manager.app.rest.request.CreateGroupRequest;
import usociety.manager.app.rest.request.UpdateGroupRequest;
import usociety.manager.app.rest.response.GetGroupResponse;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.enums.UserGroupStatusEnum;
import usociety.manager.domain.exception.GenericException;
import usociety.manager.domain.model.Category;
import usociety.manager.domain.model.Group;
import usociety.manager.domain.model.UserGroup;
import usociety.manager.domain.repository.GroupRepository;
import usociety.manager.domain.repository.UserGroupRepository;
import usociety.manager.domain.service.aws.s3.S3Service;
import usociety.manager.domain.service.category.CategoryService;
import usociety.manager.domain.service.common.CommonServiceImpl;
import usociety.manager.domain.service.email.MailService;
import usociety.manager.domain.service.group.GroupService;
import usociety.manager.domain.service.user.UserService;

@Service
public class GroupServiceImpl extends CommonServiceImpl implements GroupService {

    private static final String ERROR_UPDATING_MEMBERSHIP_ERROR_CODE = "ERROR_UPDATING_MEMBERSHIP";
    private static final String JOINING_TO_GROUP_ERROR_CODE = "ERROR_JOINING_TO_GROUP";
    private static final String UPDATING_GROUP_ERROR_CODE = "ERROR_UPDATING_GROUP";
    private static final String CREATING_GROUP_ERROR_CODE = "ERROR_CREATING_GROUP";
    private static final String GETTING_GROUP_ERROR_CODE = "ERROR_GETTING_GROUP";
    private static final String ADMINISTRATOR_ROLE = "Administrador";

    private final UserGroupRepository userGroupRepository;
    private final CategoryService categoryService;
    private final GroupRepository groupRepository;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final MailService mailService;
    private final S3Service s3Service;

    @Autowired
    public GroupServiceImpl(UserGroupRepository userGroupRepository,
                            CategoryService categoryService,
                            GroupRepository groupRepository,
                            UserService userService,
                            MailService mailService,
                            S3Service s3Service) {
        this.categoryService = categoryService;
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.s3Service = s3Service;
        objectMapper = new ObjectMapper();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public GroupApi create(String username,
                           CreateGroupRequest request,
                           MultipartFile photo)
            throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findByName(request.getName());
        if (optionalGroup.isPresent()) {
            throw new GenericException(String.format("Group with name: %s already exists.", request.getName()),
                    CREATING_GROUP_ERROR_CODE);
        }

        Category category = categoryService.get(request.getCategoryId());
        String photoUrl = s3Service.upload(photo);

        Group savedGroup;
        try {
            savedGroup = groupRepository.save(Group.newBuilder()
                    .objectives(objectMapper.writeValueAsString(request.getObjectives()))
                    .rules(objectMapper.writeValueAsString(request.getRules()))
                    .category(category)
                    .description(request.getDescription())
                    .photo(photoUrl)
                    .name(request.getName())
                    .build());

            userGroupRepository.save(UserGroup.newBuilder()
                    .group(savedGroup)
                    .isAdmin(TRUE)
                    .role(ADMINISTRATOR_ROLE)
                    .status(UserGroupStatusEnum.ACTIVE.getCode())
                    .userId(userService.get(username).getId())
                    .build());
        } catch (Exception ex) {
            s3Service.delete(photoUrl);
            throw new GenericException("Error creating group.", CREATING_GROUP_ERROR_CODE);
        }
        return Converter.group(savedGroup);
    }

    @Override
    public GetGroupResponse get(Long id, String username) throws GenericException {
        Group group = getGroup(id);
        UserApi user = userService.get(username);

        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(id, user.getId());
        if (optionalUserGroup.isPresent()) {
            UserGroup userGroup = optionalUserGroup.get();

            if (UserGroupStatusEnum.ACTIVE.getCode() == userGroup.getStatus()) {
                List<UserGroup> groupMembers = userGroupRepository.findAllByGroupId(group.getId());
                List<UserApi> activeMembers = getUsersData(groupMembers, UserGroupStatusEnum.ACTIVE, user.getId());

                GetGroupResponse.Builder builder = GetGroupResponse.newBuilder()
                        .groupApi(Converter.group(group))
                        .activeMembers(activeMembers);

                if (userGroup.isAdmin()) {
                    return builder.pendingMembers(getUsersData(groupMembers, UserGroupStatusEnum.PENDING, user.getId()))
                            .build();
                }
                return builder.build();
            }
        }

        group.setRules(null);
        return GetGroupResponse.newBuilder()
                .groupApi(Converter.group(group))
                .build();
    }

    @Override
    public Group get(Long id) throws GenericException {
        return getGroup(id);
    }

    @Override
    public List<GroupApi> getAllUserGroups(String username) throws GenericException {
        UserApi user = userService.get(username);
        return userGroupRepository
                .findAllByUserIdAndStatus(user.getId(), UserGroupStatusEnum.ACTIVE.getCode())
                .stream()
                .map(userGroup -> Converter.group(userGroup.getGroup()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateMembership(UserGroupApi request) throws GenericException {
        UserGroup userGroup = userGroupRepository.findByGroupIdAndUserId(request.getGroupId(), request.getUserId())
                .orElseThrow(() -> new GenericException("User is not member of the group.",
                        ERROR_UPDATING_MEMBERSHIP_ERROR_CODE));

        if (UserGroupStatusEnum.REJECTED == request.getStatus() || UserGroupStatusEnum.DELETED == request.getStatus()) {
            userGroupRepository.delete(userGroup);
        } else {
            userGroup.setRole(request.getRole());
            userGroup.setStatus(request.getStatus().getCode());
            userGroupRepository.save(userGroup);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void update(UpdateGroupRequest request) throws GenericException, JsonProcessingException {
        Group group = getGroup(request.getId());
        Category category = categoryService.get(request.getCategoryId());

        groupRepository.save(Group.newBuilder()
                .rules(objectMapper.writeValueAsString(request.getRules()))
                .objectives(objectMapper.writeValueAsString(request.getObjectives()))
                .description(request.getDescription())
                .photo(request.getPhoto())
                .name(request.getName())
                .category(category)
                .id(group.getId())
                .build());
    }

    @Override
    public List<GroupApi> getByFilters(String name, Long categoryId) throws GenericException {
        if (StringUtils.isEmpty(name) && Objects.isNull(categoryId)) {
            throw new GenericException("Both fields can be null at the same time,", GETTING_GROUP_ERROR_CODE);
        }

        List<Group> groupList = groupRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, name);
        groupList.addAll(groupRepository.findByCategoryIdOrNameContainingIgnoreCase(categoryId, name));
        return groupList.stream()
                .distinct()
                .map(Converter::group)
                .collect(Collectors.toList());
    }

    @Override
    public void join(Long id, String username) throws GenericException {
        Group group = getGroup(id);
        UserApi user = userService.get(username);

        Optional<UserGroup> optionalUserGroup = userGroupRepository.findByGroupIdAndUserId(id, user.getId());
        if (optionalUserGroup.isPresent()) {
            throw new GenericException("The user is already member of the group.", JOINING_TO_GROUP_ERROR_CODE);
        }

        userGroupRepository.save(UserGroup.newBuilder()
                .group(group)
                .isAdmin(FALSE)
                .status(UserGroupStatusEnum.PENDING.getCode())
                .userId(user.getId())
                .build());

        Optional<UserGroup> optionalUserGroupAdmin = userGroupRepository.findByGroupIdAndIsAdmin(id, TRUE);
        if (optionalUserGroupAdmin.isPresent()) {
            UserGroup userGroupAdmin = optionalUserGroupAdmin.get();
            UserApi userAdmin = userService.getById(userGroupAdmin.getUserId());
            mailService.send(userAdmin.getEmail(), String.format("%s ha solicitado unirse a tu grupo %s",
                    StringUtils.capitalize(user.getName()), StringUtils.capitalize(group.getName())));
        }
    }

    private Group getGroup(Long id) throws GenericException {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (!optionalGroup.isPresent()) {
            throw new GenericException(String.format("Group with id: %s no exists.", id), GETTING_GROUP_ERROR_CODE);
        }
        return optionalGroup.get();
    }

    private List<UserApi> getUsersData(List<UserGroup> groupMembers, UserGroupStatusEnum userGroupStatus, Long userId)
            throws GenericException {
        List<UserGroup> userGroups = groupMembers.stream()
                .filter(userGroup -> !userGroup.getUserId().equals(userId) &&
                        userGroupStatus.getCode() == userGroup.getStatus())
                .collect(Collectors.toList());

        List<UserApi> userApiList = new ArrayList<>();
        for (UserGroup userGroup : userGroups) {
            UserApi userApi = userService.getById(userGroup.getUserId());
            userApi.setRole(userGroup.getRole());
            userApiList.add(userApi);
        }
        return userApiList;
    }

}