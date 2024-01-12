package com.odinbook.chatservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.odinbook.chatservice.model.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {

    public List<Message> findMessagesByAccounts(Long accountId1, Long accountId2);
    public void createMessage(Message message) throws JsonProcessingException;
    public void deleteMessage(Message message);
    public void viewMessageById(Long id);
    public List<Message> findUnReadMessagesByReceiverId(Long receiverId);

}
