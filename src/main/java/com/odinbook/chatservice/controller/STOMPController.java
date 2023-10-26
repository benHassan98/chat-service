package com.odinbook.chatservice.controller;

import com.azure.core.implementation.util.ObjectsUtil;
import com.odinbook.chatservice.model.Message;
import com.odinbook.chatservice.record.AvailableFriendsRecord;
import com.odinbook.chatservice.record.NewMessageRecord;
import com.odinbook.chatservice.service.MessageService;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

@Controller
public class STOMPController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RabbitAdmin rabbitAdmin;
    private final MessageChannel notificationRequest;

    @Autowired
    public STOMPController(MessageService messageService,
                           SimpMessagingTemplate simpMessagingTemplate,
                           RabbitAdmin rabbitAdmin,
                           @Qualifier("notificationRequest") MessageChannel notificationRequest) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.rabbitAdmin = rabbitAdmin;
        this.notificationRequest = notificationRequest;
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload Message message){

    Message savedMessage = messageService.createMessage(message);

    if(Objects.nonNull(rabbitAdmin.getQueueInfo("chat."+savedMessage.getReceiverId().toString()))){
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("auto-delete",true);
        treeMap.put("durable",true);

        simpMessagingTemplate
                .convertAndSend(
                        "/queue/chat."+savedMessage.getReceiverId().toString(),
                        savedMessage,
                        treeMap
                );
    }
    else{
        notificationRequest.send(
                MessageBuilder
                        .withPayload(new NewMessageRecord(savedMessage.getSenderId(), savedMessage.getReceiverId()))
                        .setHeader("notificationType","newMessage")
                        .build()
        );
    }

    }

    @MessageMapping("/chat/availableFriends")
    public void findAvailableFriends(@Payload AvailableFriendsRecord availableFriendsRecord){

        List<Long> newFriendList = availableFriendsRecord.friendList().stream()
                .filter(
                        friend->Objects.nonNull(rabbitAdmin.getQueueInfo("chat."+friend.toString()))
                ).toList();

        simpMessagingTemplate.convertAndSend(
                "/exchange/availableFriends/"+availableFriendsRecord.accountId(),
                newFriendList
                );

    }


}
