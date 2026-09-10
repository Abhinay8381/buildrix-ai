package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.deploy.DeploymentResponse;
import com.abhinay.buildrix_ai.service.DeploymentService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesDeploymentServiceImpl implements DeploymentService {

    private final KubernetesClient kubernetesClient;

    private static final String NAMESPACE = "buildrix-apps";
    private static final String POOL_LABEL = "status";
    private static final String PROJECT_LABEL = "project-id";
    private static final String IDLE = "idle";
    private static final String BUSY = "busy";
    private static final String SYNCER_CONTAINER = "syncer";
    private static final String RUNNER_CONTAINER = "runner";
    private static final String REVERSE_PROXY_PORT = "8090";

    @Override
    public DeploymentResponse deploy(UUID projectId) {
        String domain = "project-" + projectId + "app.domain.com";

        Pod pod = findActivePod(projectId);

        if (pod != null) {
            return new DeploymentResponse("http://" + domain + ":" + REVERSE_PROXY_PORT);
        }
        return claimAndStartNewPod(projectId, domain);
    }

    private DeploymentResponse claimAndStartNewPod(UUID projectId, String domain) {
        Pod pod = kubernetesClient.pods()
                .inNamespace(NAMESPACE)
                .withLabel(POOL_LABEL, IDLE)
                .list().getItems().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No idle runners available. Please scale up the runner-pool."));

        String podName = pod.getMetadata().getName();
        log.info("Claiming pod {} for project {}", podName, projectId);

        kubernetesClient.pods().inNamespace(NAMESPACE)
                .withName(podName)
                .edit(p -> {
                    p.getMetadata().getLabels().put(POOL_LABEL, BUSY);
                    p.getMetadata().getLabels().put(PROJECT_LABEL, projectId.toString());
                    return p;
                });

        try {
            String initialSyncCmd = String.format(
                    "mc mirror --overwrite myminio/projects/%s/ /app/",
                    projectId.toString());

            log.info("Starting initial sync for project {} in pod {}", projectId, podName);
            execCommand(podName, SYNCER_CONTAINER, initialSyncCmd);

            String startCmd = "npm install && nohup npm run dev -- --host 0.0.0.0 --port 5173 > /app/dev.log 2>&1 &";

            log.info("Starting dev server for project {}...", projectId);
            execCommand(podName, RUNNER_CONTAINER, startCmd);

            log.info("Deployment successful: http://{}:{}", domain, REVERSE_PROXY_PORT);
            return new DeploymentResponse("http://" + domain + ":" + REVERSE_PROXY_PORT);

        } catch (Exception e) {
            log.error("Deployment failed for project {}. Releasing pod {}.", projectId, podName, e);
            kubernetesClient.pods().inNamespace(NAMESPACE).withName(podName).delete();
            throw new RuntimeException(e);
        }
    }

    private void execCommand(String podName, String container, String... command) {
        log.debug("Exec in {}:{} -> {}", podName, container, String.join(" ", command));

        CompletableFuture<String> data = new CompletableFuture<>();
        try (ExecWatch ignored = kubernetesClient.pods().inNamespace(NAMESPACE).withName(podName)
                .inContainer(container)
                .writingOutput(new ByteArrayOutputStream())
                .writingError(new ByteArrayOutputStream())
                .usingListener(new ExecListener() {
                    @Override
                    public void onClose(int code, String reason) {
                        data.complete("Done");
                    }
                })
                .exec(command)) {

            // Wait briefly to ensure command fired (Fabric8 exec is async)
            // For long running background jobs (nohup), we don't wait for "Done"
            if (command[command.length - 1].trim().endsWith("&")) {
                Thread.sleep(500);
            } else {
                data.get(30, TimeUnit.SECONDS); // Block for synchronous setup commands (npm install)
            }

        } catch (Exception e) {
            log.error("Exec failed", e);
            throw new RuntimeException("Pod Execution Failed", e);
        }
    }

    private Pod findActivePod(UUID projectId) {
        return kubernetesClient.pods().inNamespace(NAMESPACE)
                .withLabel(PROJECT_LABEL, projectId.toString())
                .withLabel(POOL_LABEL, BUSY)
                .list().getItems().stream()
                .filter(pod -> pod.getStatus().getPhase().equals("Running"))
                .findFirst()
                .orElse(null);
    }
}
