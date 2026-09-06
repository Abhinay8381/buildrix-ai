package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.ProjectMember;
import com.abhinay.buildrix_ai.entity.ProjectMemberId;
import com.abhinay.buildrix_ai.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    List<ProjectMember> findById_ProjectId(UUID projectId);

    @Query("""
        SELECT pm.role FROM ProjectMember pm
                WHERE pm.id.projectId = :projectId
                AND pm.id.memberId = :memberId""")
    Optional<ProjectRole> findProjectRoleByMemberIdAndProjectId(@Param("memberId") UUID memberId, @Param("projectId") UUID projectId);

    @Query("""
            SELECT COUNT(pm) FROM ProjectMember pm
            WHERE pm.id.memberId = :userId and pm.role = 'OWNER'
            """)
    Integer countProjectsByUserId(@Param("userId") UUID userId);
}
