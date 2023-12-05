package com.odinbook.chatservice.record;

import java.util.List;

public record FriendsRecord(Long accountId, List<Long> friendList) {
}
