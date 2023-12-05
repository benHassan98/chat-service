package com.odinbook.chatservice.controller;

import com.odinbook.chatservice.model.Message;
import com.odinbook.chatservice.record.FriendsRecord;
import com.odinbook.chatservice.record.NewMessageRecord;
import com.odinbook.chatservice.service.MessageService;
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

import java.util.List;
import java.util.Objects;

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

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload Message message){

    Message savedMessage = messageService.createMessage(message);

    if(Objects.nonNull(rabbitAdmin.getQueueInfo("chat."+savedMessage.getReceiverId().toString()))){
//        TreeMap<String, Object> treeMap = new TreeMap<>();
//        treeMap.put("auto-delete",true);
//        treeMap.put("durable",true);

        simpMessagingTemplate
                .convertAndSend(
                        "/queue/chat."+savedMessage.getReceiverId().toString(),
                        savedMessage
//                        ,
//                        treeMap
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

    @MessageMapping("/chat/availableFriend")
    public void availableFriend(@Payload FriendsRecord friendsRecord){

        friendsRecord.friendList().forEach(friendId->{
            if(Objects.nonNull(rabbitAdmin.getQueueInfo("chat."+friendId.toString()))){

                simpMessagingTemplate.convertAndSend(
                        "/exchange/availableFriends/availableFriend."+friendId,
                        friendsRecord.accountId()
                );

            }

        });


    }

    @MessageMapping("/chat/unAvailableFriend")
    public void unAvailableFriend(@Payload FriendsRecord friendsRecord){

        friendsRecord.friendList().forEach(friendId->{
            if(Objects.nonNull(rabbitAdmin.getQueueInfo("chat."+friendId.toString()))){

                simpMessagingTemplate.convertAndSend(
                        "/exchange/availableFriends/unAvailableFriend."+friendId,
                        friendsRecord.accountId()
                );

            }

        });


    }





    @MessageMapping("/chat/availableFriends")
    public void findAvailableFriends(@Payload FriendsRecord friendsRecord){

        List<Long> newFriendList = friendsRecord.friendList().stream()
                .filter(
                        friend->Objects.nonNull(rabbitAdmin.getQueueInfo("chat."+friend.toString()))
                ).toList();

        simpMessagingTemplate.convertAndSend(
                "/exchange/availableFriends/"+ friendsRecord.accountId(),
                newFriendList
                );

    }


    @MessageMapping("/chat/delete")
    public void deleteMessage(@Payload Message message){

        Message deletedMessage = messageService.deleteMessage(message);

        if(Objects.nonNull(rabbitAdmin.getQueueInfo("chat."+deletedMessage.getReceiverId()))){

            simpMessagingTemplate.convertAndSend(
                    "/queue/chat."+deletedMessage.getReceiverId(),
                    deletedMessage
            );

        }

    }


    @MessageMapping("/chat/view/{id}")
    public void viewMessage(@DestinationVariable("id") Long id){
        
        messageService.viewMessageById(id);
    }




}
