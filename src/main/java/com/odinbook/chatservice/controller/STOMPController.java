package com.odinbook.chatservice.controller;

import com.odinbook.chatservice.model.Message;
import com.odinbook.chatservice.record.FriendsRecord;
import com.odinbook.chatservice.record.NewMessageRecord;
import com.odinbook.chatservice.service.MessageService;
import com.odinbook.chatservice.service.STOMPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Controller
public class STOMPController {

    private final STOMPService stompService;

    @Autowired
    public STOMPController(STOMPService stompService) {
        this.stompService = stompService;
    }



    @MessageMapping("/chat/availableFriend")
    public void availableFriend(@Payload FriendsRecord friendsRecord){
        stompService.notifyFriendsOfAvailableAccount(friendsRecord);
    }

    @MessageMapping("/chat/unAvailableFriend")
    public void unAvailableFriend(@Payload FriendsRecord friendsRecord){
        stompService.notifyFriendsOfUnAvailableAccount(friendsRecord);
    }





    @MessageMapping("/chat/availableFriends")
    public void findAvailableFriends(@Payload FriendsRecord friendsRecord){
        stompService.findAvailableAccounts(friendsRecord);
    }

}
