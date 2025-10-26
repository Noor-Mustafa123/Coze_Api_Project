package com.example.api.models;

import com.coze.openapi.client.common.pagination.PageResp;
import com.coze.openapi.client.workspace.model.Workspace;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkspaceListResponseDTO {

    PageResp<Workspace> paginWorkspaceResponseObj;

    public WorkspaceListResponseDTO(PageResp<Workspace> paginWorkspaceResponseObj) {
        this.paginWorkspaceResponseObj = paginWorkspaceResponseObj;
    }

    public WorkspaceListResponseDTO() {
    }


    public PageResp<Workspace> getPaginWorkspaceResponseObj() {
        return paginWorkspaceResponseObj;
    }

    public void setPaginWorkspaceResponseObj(PageResp<Workspace> paginWorkspaceResponseObj) {
        this.paginWorkspaceResponseObj = paginWorkspaceResponseObj;
    }
}
