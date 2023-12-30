package com.odinbook.chatservice.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sender_id",nullable = false)
    private Long senderId;
    @Column(name = "receiver_id",nullable = false)
    private Long receiverId;
    @Column(name = "content")
    private String content;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "is_viewed")
    private Boolean isViewed = false;
    @Column(name = "created_date", nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp createdDate;

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getViewed() {
        return isViewed;
    }

    public void setViewed(Boolean viewed) {
        isViewed = viewed;
    }
}
