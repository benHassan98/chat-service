package com.odinbook.chatservice.record;

import java.util.List;

public record AvailableFriendsRecord(Long accountId, List<Long> friendList) {
}
