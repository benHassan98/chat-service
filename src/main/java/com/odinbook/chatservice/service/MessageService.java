package com.odinbook.chatservice.service;

import com.odinbook.chatservice.model.Message;

import java.util.List;

public interface MessageService {

    public List<Message> findMessagesByAccounts(Long accountId1, Long accountId2);
    public Message createMessage(Message message);

}
