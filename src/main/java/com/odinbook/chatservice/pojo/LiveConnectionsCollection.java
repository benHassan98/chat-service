package com.odinbook.chatservice.pojo;



import org.antlr.v4.runtime.misc.Pair;

import java.util.HashSet;

public class LiveConnectionsCollection {
    private final HashSet<Pair<String, String>> collection;

    public LiveConnectionsCollection() {
        this.collection = new HashSet<>();
    }

    public HashSet< Pair<String, String> > getCollection() {
        return collection;
    }
}
