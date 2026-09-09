package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.Project;
import com.abhinay.buildrix_ai.enums.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("""
            SELECT p as project, pm.role as role
            FROM Project p
            JOIN ProjectMember pm on p.id = pm.project.id
            WHERE p.deletedAt is NULL
            AND pm.member.id = :userId
            ORDER BY p.updatedAt DESC
        """) //TODO: Add project member logic also here
    List<ProjectWithRole> findAllAccessibleByUser(@Param("userId") UUID usedId);

    @Query("""
        SELECT p from Project p
        WHERE p.id = :id
        AND EXISTS(
                SELECT 1 FROM ProjectMember pm
                WHERE pm.project.id = :id
                AND pm.member.id = :userId
            )
        AND p.deletedAt IS NULL
""")
    Optional<Project> findAccessibleProjectById(@Param("userId") UUID userId, @Param("id") UUID id);

    @Query("""
        SELECT p as project, pm.role as role
        FROM Project p
        JOIN ProjectMember pm ON pm.project.id = p.id
        WHERE p.id = :id AND
        pm.member.id = :userId
        AND p.deletedAt IS NULL
""")
    Optional<ProjectWithRole> findAccessibleProjectByIdWithRole(@Param("userId") UUID userId, @Param("id") UUID id);


    interface ProjectWithRole{
        Project getProject();
        ProjectRole getRole();
    }
}
