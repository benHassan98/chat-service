package com.odinbook.chatservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.odinbook.chatservice.model.Message;
import com.odinbook.chatservice.record.NewMessageRecord;
import com.odinbook.chatservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.TreeMap;

@RestController
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/messages/{accountId1}/{accountId2}")
    public ResponseEntity<?> findMessagesByChat(@PathVariable Long accountId1, @PathVariable Long accountId2){
        return ResponseEntity.ok(messageService.findMessagesByAccounts(accountId1,accountId2));
    }

    @GetMapping("/message/unRead/{receiverId}")
    public ResponseEntity<?> findUnReadMessageByReceiverId(@PathVariable Long receiverId){

        return ResponseEntity.ok(messageService.findUnReadMessagesByReceiverId(receiverId));
    }

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload Message message){
        try{
            messageService.createMessage(message);
        }
        catch (JsonProcessingException exception){
            exception.printStackTrace();
        }
    }
    @MessageMapping("/chat/delete")
    public void deleteMessage(@Payload Message message){
        messageService.deleteMessage(message);
    }


    @MessageMapping("/chat/view/{id}")
    public void viewMessage(@DestinationVariable("id") Long id){
        messageService.viewMessageById(id);
    }


}
