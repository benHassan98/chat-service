package com.odinbook.chatservice.controller;

import com.odinbook.chatservice.model.Message;
import com.odinbook.chatservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/message/images")
    public void createMessage(
            @RequestParam(value = "idList",required = false) String[] idList,
            @RequestParam(value = "fileList",required = false) MultipartFile[] fileList){

        messageService.saveMessageImages(idList, fileList);
    }

}
