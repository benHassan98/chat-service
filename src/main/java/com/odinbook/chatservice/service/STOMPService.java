package com.odinbook.chatservice.service;

import com.odinbook.chatservice.record.FriendsRecord;

public interface STOMPService {
    public void notifyFriendsOfAvailableAccount(FriendsRecord friendsRecord);
    public void notifyFriendsOfUnAvailableAccount(FriendsRecord friendsRecord);
    public void findAvailableAccounts(FriendsRecord friendsRecord);

}
