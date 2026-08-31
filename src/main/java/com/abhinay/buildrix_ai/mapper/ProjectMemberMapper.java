package com.abhinay.buildrix_ai.mapper;

import com.abhinay.buildrix_ai.dto.project.member.ProjectMemberResponse;
import com.abhinay.buildrix_ai.entity.ProjectMember;
import com.abhinay.buildrix_ai.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMemberMapper {

    @Mapping(target = "projectRole", constant = "OWNER")
    @Mapping(target = "userId", source = "id")
    ProjectMemberResponse toProjectMemberResponseFromOwner(User user);

    @Mapping(target = "userId", source = "member.id")
    @Mapping(target = "email", source = "member.email")
    @Mapping(target = "name", source = "member.name")
    @Mapping(target = "projectRole", source = "role")
    ProjectMemberResponse toProjectMemberResponseFromProjectMember(ProjectMember projectMember);
}
