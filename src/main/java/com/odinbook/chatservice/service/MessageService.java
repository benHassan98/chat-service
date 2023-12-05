package com.odinbook.chatservice.service;

import com.odinbook.chatservice.model.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {

    public List<Message> findMessagesByAccounts(Long accountId1, Long accountId2);
    public Message createMessage(Message message);
    public void saveMessageImages(String[] idList, MultipartFile[] fileList);
    public Message deleteMessage(Message message);
    public void viewMessageById(Long id);
    public List<Message> findUnReadMessagesByReceiverId(Long receiverId);

}
