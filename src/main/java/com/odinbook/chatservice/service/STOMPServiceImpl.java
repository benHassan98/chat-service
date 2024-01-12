package com.odinbook.chatservice.service;

import com.odinbook.chatservice.pojo.LiveConnectionsCollection;
import com.odinbook.chatservice.record.FriendsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class STOMPServiceImpl implements STOMPService{

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LiveConnectionsCollection connectionsCollection;

    @Autowired
    public STOMPServiceImpl(SimpMessagingTemplate simpMessagingTemplate,
                            LiveConnectionsCollection connectionsCollection) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.connectionsCollection = connectionsCollection;
    }

    @Override
    public void notifyFriendsOfAvailableAccount(FriendsRecord friendsRecord) {

        friendsRecord.friendList().forEach(friendId->{
            if(connectionsCollection.getCollection().stream().anyMatch(pair->pair.b.equals("/queue/chat."+friendId.toString()))){

                simpMessagingTemplate.convertAndSend(
                        "/exchange/availableFriends/availableFriend."+friendId,
                        friendsRecord.accountId()
                );

            }

        });
    }

    @Override
    public void notifyFriendsOfUnAvailableAccount(FriendsRecord friendsRecord) {

        friendsRecord.friendList().forEach(friendId->{
            if(connectionsCollection.getCollection().stream().anyMatch(pair->pair.b.equals("/queue/chat."+friendId.toString()))){

                simpMessagingTemplate.convertAndSend(
                        "/exchange/availableFriends/unAvailableFriend."+friendId,
                        friendsRecord.accountId()
                );

            }

        });
    }

    @Override
    public void findAvailableAccounts(FriendsRecord friendsRecord) {
        List<Long> newFriendList = friendsRecord.friendList().stream()
                .filter(
                        friendId->connectionsCollection.getCollection().stream().anyMatch(pair->pair.b.equals("/queue/chat."+friendId.toString()))
                ).toList();

        simpMessagingTemplate.convertAndSend(
                "/exchange/availableFriends/"+friendsRecord.accountId(),
                newFriendList
        );

    }
}
