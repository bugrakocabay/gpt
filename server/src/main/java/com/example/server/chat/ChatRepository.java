package com.example.server.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {

    public Chat findByConversationId(String conversation_id);
}
