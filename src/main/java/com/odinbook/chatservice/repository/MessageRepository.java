package com.odinbook.chatservice.repository;

import com.odinbook.chatservice.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(value = """
            SELECT *
            FROM messages
            WHERE
            (sender_id = :accountId1 AND receiver_id = :accountId2) OR
            (sender_id = :accountId2 AND receiver_id = :accountId1)""",nativeQuery = true)
    public List<Message> findMessagesByAccounts(@Param("accountId1") Long accountId1,
                                            @Param("accountId2") Long accountId2);

    @Query(value = "SELECT * FROM messages WHERE receiver_id = :receiverId AND is_deleted = 0 AND is_viewed = 0", nativeQuery = true)
    public List<Message> findUnReadMessagesByReceiverId(@Param("receiverId") Long receiverId);


}
