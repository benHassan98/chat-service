package com.odinbook.chatservice.controller;

import com.odinbook.chatservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{accountId1}/{accountId2}")
    public ResponseEntity<?> findMessagesByChat(@PathVariable Long accountId1, @PathVariable Long accountId2){
        return ResponseEntity.ok(messageService.findMessagesByAccounts(accountId1,accountId2));
    }



}
