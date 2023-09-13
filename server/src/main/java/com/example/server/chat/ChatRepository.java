package com.example.server.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {

    Chat findByConversationId(String conversation_id);
    List<Chat> getChatsByUserId(String user_id);
}
