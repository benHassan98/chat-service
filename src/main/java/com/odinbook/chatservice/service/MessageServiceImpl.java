package com.odinbook.chatservice.service;

import com.odinbook.chatservice.model.Message;
import com.odinbook.chatservice.repository.MessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{

    @PersistenceContext
    private EntityManager entityManager;
    private final MessageRepository messageRepository;


    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Message> findMessagesByAccounts(Long accountId1, Long accountId2) {
        return messageRepository.findMessagesByAccounts(accountId1, accountId2);
    }

    @Override
    public Message createMessage(Message message) {

        return messageRepository.saveAndFlush(message);
    }

    @Override
    public Message deleteMessage(Message message) {

        message.setDeleted(true);
        message.setContent("");

        return messageRepository.saveAndFlush(message);

    }

    @Override
    @Transactional
    public void viewMessageById(Long id) {

        entityManager
                .createNativeQuery("UPDATE messages SET is_viewed = true WHERE id = :id")
                .setParameter("id",id)
                .executeUpdate();

    }

    @Override
    public List<Message> findUnReadMessagesByReceiverId(Long receiverId) {
        return messageRepository.findUnReadMessagesByReceiverId(receiverId);
    }
}
