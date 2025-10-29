package com.example.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserQueryDTO {

    @JsonProperty("chat_id")
    public String chatId;

    @JsonProperty("user_id")
    public String userId;

    @JsonProperty("bot_id")
    public String botId;

    @JsonProperty("message")
    public String message;


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }


}
