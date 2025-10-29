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

    @GetMapping("/create/chat")
    public ResponseEntity createNewChat(@RequestParam("botId") String botId, @RequestParam("userId") String userId ){
        CreateChatReq createChatReq =  CreateChatReq.builder().botID(botId).userID(userId).build();
        CreateChatResp createChatResp = cozeAPI.chat().create(createChatReq);
        // ! feature:: create a entity for chat and save it to db for now im just reutnring the chatid
        Chat chatModel = createChatResp.getChat();
        return ResponseEntity.ok(createChatResp);
    }

    @PostMapping("/{chatId}/input")
    public ResponseEntity chatUserInput(@PathVariable("chatId") String chatId, @RequestBody UserQueryDTO userQueryDTO){
        Message message = Message.builder().chatId(userQueryDTO.chatId).botId(userQueryDTO.botId).content(userQueryDTO.getMessage()).contentType(MessageContentType.TEXT).build();
        CreateChatReq createChatReq =  CreateChatReq.builder().botID(userQueryDTO.getBotId()).userID(userQueryDTO.userId).messages(List.of(message)).build();
        ChatPoll createChatResponse;
        try {
            createChatResponse = cozeAPI.chat().createAndPoll(createChatReq);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        // temporary for testing
        System.out.println(createChatResponse.getMessages().get(0));
        return ResponseEntity.ok(createChatResponse);
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
