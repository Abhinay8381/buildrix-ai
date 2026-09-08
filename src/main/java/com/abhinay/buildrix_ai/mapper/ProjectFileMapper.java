package com.abhinay.buildrix_ai.mapper;

import com.abhinay.buildrix_ai.dto.project.file.FileNode;
import com.abhinay.buildrix_ai.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
