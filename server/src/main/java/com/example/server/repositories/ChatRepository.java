package com.example.server.repositories;

import com.example.server.models.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, String> {

    Optional<Chat> findByConversationId(String conversation_id);
    List<Chat> getChatsByUserId(String user_id);
}
