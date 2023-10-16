package com.odinbook.chatservice.config;

import com.azure.core.http.rest.RequestOptions;
import com.azure.messaging.webpubsub.WebPubSubServiceClient;
import com.azure.messaging.webpubsub.WebPubSubServiceClientBuilder;
import com.azure.messaging.webpubsub.models.GetClientAccessTokenOptions;
import com.azure.messaging.webpubsub.models.WebPubSubClientAccessToken;
import com.azure.messaging.webpubsub.models.WebPubSubContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odinbook.chatservice.model.Message;
import com.odinbook.chatservice.service.MessageService;
import jakarta.annotation.PostConstruct;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class WebPubSubConfig {
    @Value("${spring.cloud.azure.pubsub.connection-string}")
    private String webPubSubConnectStr;
    private final MessageService messageService;
    private final MessageChannel notificationRequest;

    @Autowired
    public WebPubSubConfig(MessageService messageService,
                           @Qualifier("notificationRequest") MessageChannel notificationRequest) {
        this.messageService = messageService;
        this.notificationRequest = notificationRequest;
    }

    @PostConstruct
    public void init() throws URISyntaxException {
        WebPubSubServiceClient service = new WebPubSubServiceClientBuilder()
                .connectionString(webPubSubConnectStr)
                .hub("chat")
                .buildClient();

        WebPubSubClientAccessToken token = service.getClientAccessToken(
                new GetClientAccessTokenOptions()
                        .setUserId("0")
        );
        WebSocketClient webSocketClient = new WebSocketClient(new URI(token.getUrl())) {

            @Override
            public void onMessage(String jsonString) {
                try {
                    Message message = new ObjectMapper().readValue(jsonString, Message.class);
                    Message savedMessage = messageService.createMessage(message);
                    String savedMessageJson = new ObjectMapper().writeValueAsString(savedMessage);

                    if(service
                            .userExistsWithResponse(savedMessage.getReceiverId().toString(),new RequestOptions())
                            .getValue()
                    ){
                        service.sendToUser(savedMessage.getReceiverId().toString(),
                                savedMessageJson,
                                WebPubSubContentType.APPLICATION_JSON);
                    }
                    else{
                        notificationRequest.send(
                                MessageBuilder
                                        .withPayload(savedMessage)
                                        .setHeader("notificationType","newMessage")
                                        .build()
                        );
                    }


                } catch (JsonProcessingException exception) {
                    exception.printStackTrace();
                }

            }

            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onClose(int i, String s, boolean b) {

            }

            @Override
            public void onError(Exception e) {

            }
        };
        webSocketClient.connect();

    }



}
