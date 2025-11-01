package com.example.api.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.coze.openapi.client.chat.CreateChatReq;
import com.coze.openapi.client.chat.CreateChatResp;
import com.coze.openapi.client.chat.model.Chat;
import com.coze.openapi.client.chat.model.ChatPoll;
import com.coze.openapi.client.connversations.message.model.Message;
import com.coze.openapi.client.connversations.message.model.MessageContentType;
import com.coze.openapi.service.service.chat.ChatMessageService;
import com.example.api.models.UserQueryDTO;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

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
import com.example.api.service.APIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@CrossOrigin(origins = "http://localhost:63342")
@RequestMapping( "/v1" )
@RestController
public class APIController {

    @Autowired
    public CozeAPI cozeAPI;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public APIService apiService;

    /* ************************WORKSPACES************************ */

    @GetMapping( "/workspaces/list" )
    public ResponseEntity< WorkspaceListResponseDTO > createChat() {
        return ResponseEntity.ok( apiService.createRequest() );
    }

    /* ************************CHAT************************ */

    @GetMapping( "/create/bot" )
    public ResponseEntity createBotInWorkspace( @RequestParam( "id" ) String workspaceId ) {
        PublishBotResp publishBotResp = apiService.createBot( workspaceId );
        return new ResponseEntity<>( publishBotResp, HttpStatusCode.valueOf( 200 ) );

    }

    @GetMapping("/create/chat")
    public ResponseEntity createNewChat(@RequestParam("botId") String botId, @RequestParam("userId") String userId ){
        CreateChatReq createChatReq =  CreateChatReq.builder().botID(botId).userID(userId).build();
                return ResponseEntity.ok(apiService.createChat( createChatReq ));
    }

    @PostMapping("/{chatId}/input")
    public ResponseEntity chatUserInput(@PathVariable("chatId") String chatId, @RequestBody UserQueryDTO userQueryDTO){
        ChatPoll queryResponse = apiService.chatUserInput(chatId, userQueryDTO );
        // temporary for testing
        System.out.println(queryResponse.getMessages().get(0));
        return ResponseEntity.ok(queryResponse);
    }



    // if wanting to use the okhttp client instead of the default springboot client
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
