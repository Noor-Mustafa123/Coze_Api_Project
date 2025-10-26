package com.example.api.controllers;

import com.coze.openapi.client.common.pagination.PageResp;
import com.coze.openapi.client.common.pagination.PageResponse;
import com.coze.openapi.client.workspace.ListWorkspaceReq;
import com.coze.openapi.client.workspace.model.Workspace;
import com.coze.openapi.service.service.CozeAPI;
import com.example.api.models.WorkspaceListResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RequestMapping("/v1/workspaces")
@RestController
public class APIController {

    @Autowired
    public CozeAPI cozeAPI;

    @Autowired
    public ObjectMapper objectMapper;

    @GetMapping("/list")
    public ResponseEntity<WorkspaceListResponseDTO> createChat() {
        ListWorkspaceReq listWorkspaceReq = ListWorkspaceReq.builder().build();
        PageResp<Workspace> paginatedResponse = cozeAPI.workspaces().list(listWorkspaceReq);
        List<Workspace> dataBuffer = new ArrayList<>();
        while(paginatedResponse.getIterator().hasNext()){
            dataBuffer.add(paginatedResponse.getIterator().next());
        }
        WorkspaceListResponseDTO response = new WorkspaceListResponseDTO();
        response.setPaginWorkspaceResponseObj(paginatedResponse);
        return ResponseEntity.ok(response);

    }

    public Response getOkHttpResponseObject(Object body) {
        String responseJson;
        try {
            responseJson = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Request dummyRequest = new Request.Builder().url("http://localhost/").build();
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        ResponseBody responseBody = ResponseBody.create(mediaType,responseJson);
        return new Response.Builder().request(dummyRequest).protocol(Protocol.HTTP_1_1).code(200).message("ok").body(responseBody).build();
    }
}
