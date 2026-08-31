package com.abhinay.buildrix_ai.controller;

import com.abhinay.buildrix_ai.dto.project.member.InviteMemberRequest;
import com.abhinay.buildrix_ai.dto.project.member.ProjectMemberResponse;
import com.abhinay.buildrix_ai.dto.project.member.UpdateProjectMemberRequest;
import com.abhinay.buildrix_ai.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;
    private static final UUID userId = UUID.fromString("32aea559-aed5-45c5-bfec-23dcc3ef70c5");

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponse>> getAllProjectMembers(@PathVariable UUID projectId){
        return ResponseEntity.ok(projectMemberService.getAllProjectMembers(userId, projectId));
    }

    @PostMapping
    public ResponseEntity<ProjectMemberResponse> inviteMember(@PathVariable UUID projectId,
                                         @RequestBody InviteMemberRequest request){

        return ResponseEntity.status(201)
                .body(projectMemberService.inviteMember(userId, projectId, request));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<ProjectMemberResponse> updateMemberRole(@PathVariable UUID memberId,
                                                                  @PathVariable UUID projectId,
                                                              @RequestBody UpdateProjectMemberRequest request){

        return ResponseEntity.status(200)
                .body(projectMemberService.updateMemberRole(userId, projectId, memberId, request));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable UUID memberId,
                                                                  @PathVariable UUID projectId){
        projectMemberService.removeMember(userId, projectId, memberId);
        return ResponseEntity.noContent().build();
    }
}
