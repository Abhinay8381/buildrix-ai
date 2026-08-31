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
                            AND p.owner.id = :userId
                                    ORDER BY p.updatedAt DESC
        """) //TODO: Add project member logic also here
    List<Project> findAllAccessibleByUser(@Param("userId") UUID usedId);

    @Query("""
        SELECT p from Project p
        LEFT JOIN FETCH p.owner
        WHERE p.id = :id
        AND p.deletedAt IS NULL
        AND p.owner.id = :userId
""")
    Optional<Project> findAccessibleProjectById(@Param("userId") UUID userId, @Param("id") UUID id);

}
