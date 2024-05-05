package com.odinbook.chatservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.odinbook.chatservice.model.Message;
import com.odinbook.chatservice.pojo.LiveConnectionsCollection;
import com.odinbook.chatservice.record.NewMessageRecord;
import com.odinbook.chatservice.repository.MessageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class MessageServiceImpl implements MessageService{

    @PersistenceContext
    private EntityManager entityManager;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LiveConnectionsCollection connectionsCollection;
    private final StringRedisTemplate stringRedisTemplate;


    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,
                              StringRedisTemplate stringRedisTemplate,
                              SimpMessagingTemplate simpMessagingTemplate,
                              LiveConnectionsCollection connectionsCollection) {
        this.messageRepository = messageRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.connectionsCollection = connectionsCollection;
    }

    @Override
    public List<Message> findMessagesByAccounts(Long accountId1, Long accountId2) {
        return messageRepository.findMessagesByAccounts(accountId1, accountId2);
    }

    @Override
    public void createMessage(Message message) throws JsonProcessingException {

        Message savedMessage = messageRepository.saveAndFlush(message);

        simpMessagingTemplate
                .convertAndSend(
                        "/queue/chat."+savedMessage.getSenderId().toString(),
                        savedMessage
                );


        if(connectionsCollection.getCollection().stream().anyMatch(pair->pair.b.equals("/queue/chat."+savedMessage.getReceiverId().toString()))){

            simpMessagingTemplate
                    .convertAndSend(
                            "/queue/chat."+savedMessage.getReceiverId().toString(),
                            savedMessage
                    );

        }
        else{

            NewMessageRecord newMessageRecord = new NewMessageRecord(savedMessage.getSenderId(), savedMessage.getReceiverId());

            String messageJson = new ObjectMapper().writeValueAsString(newMessageRecord);

            stringRedisTemplate.convertAndSend("newMessageChannel",messageJson);

        }



    }

    @Override
    public void deleteMessage(Message message) {

        message.setDeleted(true);
        message.setContent("");
        messageRepository.saveAndFlush(message);
        
        if(connectionsCollection.getCollection().stream().anyMatch(pair->pair.b.equals("/queue/chat."+message.getReceiverId()))){

            simpMessagingTemplate.convertAndSend(
                    "/queue/chat."+message.getReceiverId(),
                    message
            );

        }

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
