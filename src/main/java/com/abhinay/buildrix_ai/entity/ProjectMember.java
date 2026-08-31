package com.abhinay.buildrix_ai.entity;

import com.abhinay.buildrix_ai.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "project_members")
public class ProjectMember {

    @EmbeddedId
    private ProjectMemberId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    private Instant invitedAt;
    private Instant acceptedAt;

    @ManyToOne
    @MapsId("projectId")
    private Project project;

    @ManyToOne
    @MapsId("memberId")
    private User member;
}
