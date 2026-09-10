package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.deploy.DeploymentResponse;

import java.util.UUID;

public interface DeploymentService {

     DeploymentResponse deploy(UUID projectId);
}
