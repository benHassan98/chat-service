package com.odinbook.chatservice.service;

import com.odinbook.chatservice.model.Message;
import com.odinbook.chatservice.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final ImageService imageService;


    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,
                              ImageService imageService) {
        this.messageRepository = messageRepository;
        this.imageService = imageService;
    }

    @Override
    public List<Message> findMessagesByAccounts(Long accountId1, Long accountId2) {
        return messageRepository.findMessagesByAccounts(accountId1, accountId2);
    }

    @Override
    public Message createMessage(Message message) {

        message.setId(messageRepository.saveAndFlush(message).getId());

        imageService.createBlobs(
                "message."+message.getId(),
                message.getImageList()
        );

        String newContent = imageService.injectImagesToHTML(message.getContent(),
                message.getImageList());

        message.setContent(newContent);

        return messageRepository.saveAndFlush(message);
    }
}
