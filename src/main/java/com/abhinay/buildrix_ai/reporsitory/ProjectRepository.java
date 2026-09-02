package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("""
            SELECT p from Project p
            WHERE p.deletedAt is NULL
            AND EXISTS(
                SELECT 1 FROM ProjectMember pm
                WHERE pm.project.id = p.id
                AND pm.member.id = :userId
            )
            ORDER BY p.updatedAt DESC
        """) //TODO: Add project member logic also here
    List<Project> findAllAccessibleByUser(@Param("userId") UUID usedId);

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

}
