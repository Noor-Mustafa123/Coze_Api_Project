package com.example.api.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coze.openapi.client.bots.CreateBotReq;
import com.coze.openapi.client.bots.CreateBotResp;
import com.coze.openapi.client.bots.PublishBotReq;
import com.coze.openapi.client.bots.PublishBotResp;
import com.coze.openapi.client.bots.model.BotOnboardingInfo;
import com.coze.openapi.client.bots.model.BotPromptInfo;
import com.coze.openapi.client.common.pagination.PageResp;
import com.coze.openapi.client.files.UploadFileReq;
import com.coze.openapi.client.files.UploadFileResp;
import com.coze.openapi.client.workspace.ListWorkspaceReq;
import com.coze.openapi.client.workspace.model.Workspace;
import com.coze.openapi.service.service.CozeAPI;
import com.example.api.models.WorkspaceListResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@RequestMapping( "/v1" )
@RestController
public class APIController {

    @Autowired
    public CozeAPI cozeAPI;

    @Autowired
    public ObjectMapper objectMapper;

    /* ************************WORKSPACES************************ */

    @GetMapping( "/workspaces/list" )
    public ResponseEntity< WorkspaceListResponseDTO > createChat() {
        ListWorkspaceReq listWorkspaceReq = ListWorkspaceReq.builder().build();
        PageResp< Workspace > paginatedResponse = cozeAPI.workspaces().list( listWorkspaceReq );
        List< Workspace > dataBuffer = new ArrayList<>();
        while ( paginatedResponse.getIterator().hasNext() ) {
            dataBuffer.add( paginatedResponse.getIterator().next() );
        }
        WorkspaceListResponseDTO response = new WorkspaceListResponseDTO();
        response.setPaginWorkspaceResponseObj( paginatedResponse );
        return ResponseEntity.ok( response );

    }

    /* ************************CHAT************************ */

    @GetMapping( "/create/bot" )
    public ResponseEntity createBotInWorkspace( @RequestParam( "id" ) String workspaceId ) {

        // bot avatar upload
        UploadFileResp uploadFileResp = cozeAPI.files().upload( UploadFileReq.of( "/home/sces82/Downloads/51ICajOAAXL.jpg" ) );
        String fileID = uploadFileResp.getFileInfo().getID();

        // set the onboarding info of your bot
        BotOnboardingInfo botOnboardingInfo = BotOnboardingInfo.builder().prologue( "Welcome to Bot" )
                .suggestedQuestions( Arrays.asList( "add your question", "question 2" ) ).build();

        // bot prompt info
        String prompt = "You are a helpful and concise assistant designed to answer user questions clearly and directly.  \n"
                + "Your goal is to provide factual, relevant, and structured responses without unnecessary elaboration.  \n"
                + "If the user’s question is ambiguous, request clarification before proceeding.  \n"
                + "Always maintain a neutral tone and avoid speculation.  ";
        BotPromptInfo botPromptInfo = BotPromptInfo.builder().prompt( "sample prompt" ).build();

        //create bot request
        CreateBotReq createBotReq = CreateBotReq.builder().name( "first-bot" ).iconFileID( fileID ).onboardingInfo( botOnboardingInfo )
                .promptInfo( botPromptInfo ).spaceID( workspaceId ).build();

        CreateBotResp createBotResp = cozeAPI.bots().create( createBotReq );

        String botID = createBotResp.getBotID();

        // Publish bot request
        PublishBotReq publishBotReq = PublishBotReq.builder().botID( botID ).connectorIDs( Arrays.asList("1024")).build();

        PublishBotResp publishBotResp = cozeAPI.bots().publish( publishBotReq );

        return new ResponseEntity<>( publishBotResp, HttpStatusCode.valueOf( 200 ) );

    }

    public Response getOkHttpResponseObject( Object body ) {
        String responseJson;
        try {
            responseJson = objectMapper.writeValueAsString( body );
        } catch ( JsonProcessingException e ) {
            throw new RuntimeException( e );
        }
        Request dummyRequest = new Request.Builder().url( "http://localhost/" ).build();
        MediaType mediaType = MediaType.get( "application/json; charset=utf-8" );
        ResponseBody responseBody = ResponseBody.create( mediaType, responseJson );
        return new Response.Builder().request( dummyRequest ).protocol( Protocol.HTTP_1_1 ).code( 200 ).message( "ok" ).body( responseBody )
                .build();

    }

}
