package com.odinbook.chatservice.component;

import com.odinbook.chatservice.pojo.LiveConnectionsCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class LiveConnectionsCollectionBean {
    @Bean
    public LiveConnectionsCollection liveConnectionsCollection(){
        return new LiveConnectionsCollection();
    }
}
