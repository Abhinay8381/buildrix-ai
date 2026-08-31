package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.ProjectMember;
import com.abhinay.buildrix_ai.entity.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    List<ProjectMember> findById_ProjectId(UUID projectId);
}
