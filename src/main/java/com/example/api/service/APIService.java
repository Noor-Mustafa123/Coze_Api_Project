package com.example.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.coze.openapi.client.bots.CreateBotReq;
import com.coze.openapi.client.bots.CreateBotResp;
import com.coze.openapi.client.bots.PublishBotReq;
import com.coze.openapi.client.bots.PublishBotResp;
import com.coze.openapi.client.bots.model.BotOnboardingInfo;
import com.coze.openapi.client.bots.model.BotPromptInfo;
import com.coze.openapi.client.chat.CreateChatReq;
import com.coze.openapi.client.chat.CreateChatResp;
import com.coze.openapi.client.chat.model.Chat;
import com.coze.openapi.client.chat.model.ChatPoll;
import com.coze.openapi.client.common.pagination.PageResp;
import com.coze.openapi.client.connversations.message.model.Message;
import com.coze.openapi.client.connversations.message.model.MessageContentType;
import com.coze.openapi.client.files.UploadFileReq;
import com.coze.openapi.client.files.UploadFileResp;
import com.coze.openapi.client.workspace.ListWorkspaceReq;
import com.coze.openapi.client.workspace.model.Workspace;
import com.coze.openapi.service.service.CozeAPI;
import com.example.api.models.UserQueryDTO;
import com.example.api.models.WorkspaceListResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class APIService {

    @Autowired
    public CozeAPI cozeAPI;

    @Autowired
    public ObjectMapper objectMapper;

    public WorkspaceListResponseDTO createRequest() {
        ListWorkspaceReq listWorkspaceReq = ListWorkspaceReq.builder().build();
        PageResp< Workspace > paginatedResponse = cozeAPI.workspaces().list( listWorkspaceReq );
        WorkspaceListResponseDTO response = new WorkspaceListResponseDTO(paginatedResponse);
        return response;
    }

    public PublishBotResp createBot(String workspaceId) {
        // bot avatar upload
        UploadFileResp uploadFileResp = cozeAPI.files().upload( UploadFileReq.of( "/home/noor/Downloads/kali-cubism.jpg" ) );
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

        return publishBotResp;
    }

    public Chat createChat( CreateChatReq createChatReq){
        CreateChatResp createChatResp = cozeAPI.chat().create(createChatReq);
        // ! feature:: create a entity for chat and save it to db for now im just reutnring the chatid
        Chat chatModel = createChatResp.getChat();
        return chatModel;
    }

    public ChatPoll chatUserInput( UserQueryDTO userQueryDTO){
        Message message = Message.builder().chatId(userQueryDTO.chatId).botId(userQueryDTO.botId).content(userQueryDTO.getMessage()).contentType(
                MessageContentType.TEXT).build();
        CreateChatReq createChatReq =  CreateChatReq.builder()
                .botID(userQueryDTO.getBotId())
                .userID(userQueryDTO.getUserId())
                .messages(List.of(message))
                .build();
        ChatPoll queryResponse;
        try {
            queryResponse = cozeAPI.chat().createAndPoll(createChatReq);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return queryResponse;
    }



}
