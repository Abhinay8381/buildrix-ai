package com.abhinay.buildrix_ai.llm.tools;

import com.abhinay.buildrix_ai.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class CodeGenerationTool {

    private final UUID projectId;
    private final ProjectFileService projectFileService;

    @Tool(name = "read_files",
            description = "Read the content of files. Only input the file names present inside the FILE_TREE. DO NOT input any path which is not present under the FILE_TREE."

    )
    List<String> getFileContent(@ToolParam(description = "List of relative paths (e.g., ['src/App.tsx'])")
                                List<String> paths){

        List<String> fileContents = new ArrayList<>();
        for(String path : paths){
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;

            log.info("Requested file: {}", cleanPath);

            String fileContent = projectFileService.getFileContent(projectId, path).content();

            fileContents.add(String.format(
                    "--- START OF FILE: %s ---\n%s\n--- END OF FILE ---",
                    cleanPath, fileContent));
        }
        return fileContents;
    }
}
