package com.odinbook.chatservice.component;


import com.odinbook.chatservice.pojo.LiveConnectionsCollection;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Arrays;

@Component
public class WebSocketEventListener {


    @Autowired
    private LiveConnectionsCollection connectionsCollection;

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        String simpSessionId = (String) message.getHeaders().get("simpSessionId");
        String simpDestination = (String) message.getHeaders().get("simpDestination");

        connectionsCollection.getCollection().add(new Pair<>(simpSessionId, simpDestination));

    }
    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        Message<byte[]> message = event.getMessage();
        String simpSessionId = (String) message.getHeaders().get("simpSessionId");

        connectionsCollection.getCollection().removeIf((pair)->pair.a.equals(simpSessionId));

    }

}
