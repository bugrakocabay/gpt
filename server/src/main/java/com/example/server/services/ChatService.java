package com.example.server.services;

import com.example.server.dto.requests.SaveChatRequest;
import com.example.server.dto.responses.ChatResponse;
import com.example.server.models.Message;
import com.example.server.dto.requests.UpdateChatRequest;
import com.example.server.exceptions.NotFoundException;
import com.example.server.models.Chat;
import com.example.server.repositories.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    private final Environment environment;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Logger logger = Logger.getLogger(ChatService.class.getName());

    public ChatService(Environment environment) {
        this.environment = environment;
    }

    @Transactional
    public Chat getChatById(String id) {
        logger.info("Getting chat with id: " + id);
        Optional<Chat> chatOptional = chatRepository.findByConversationId(id);
        logger.info("Chat found: " + chatOptional);

        if (chatOptional.isEmpty()) {
            throw new NotFoundException("Chat not found");
        }
        return chatOptional.get();
    }

    @Transactional
    public Chat saveChatWithId(SaveChatRequest saveChatRequest) {
        logger.info("Saving chat with id: " + saveChatRequest.getId());
        Chat chat = new Chat();
        chat.setConversationId(saveChatRequest.getId());
        chat.setUserId(saveChatRequest.getUserId());
        return chatRepository.save(chat);
    }

    @Transactional
    @PreAuthorize("#userId == authentication.principal.id")
    public List<Chat> getAllChats(String userId) {
        logger.info("Getting all chats");
        return chatRepository.getChatsByUserId(userId);
    }

    @Transactional
    public ChatResponse updateChatMessage(UpdateChatRequest requestBody) throws JsonProcessingException {
        logger.info("Updating chat with id: " + requestBody.getId());
        Optional<Chat> chat = chatRepository.findByConversationId(requestBody.getId());
        String userMessage = requestBody.getMessage();
        if (chat.isEmpty()) {
            throw new NotFoundException("Chat not found");
        }
        Chat foundChat = chat.get();

        String openAIResponse = sendChatRequest(userMessage).join();
        JsonNode jsonResponse;

        ObjectMapper objectMapper = new ObjectMapper();
        jsonResponse = objectMapper.readTree(openAIResponse);
        String aiResponse = jsonResponse
                .get("choices")
                .get(0)
                .get("message")
                .get("content")
                .asText();

        Message newMessage = new Message(requestBody.getMessage(), aiResponse);
        Message[] currentMessages = foundChat.getMessage();
        if (currentMessages == null) {
            currentMessages = new Message[0];
        }
        Message[] updatedMessages = Arrays.copyOf(currentMessages, currentMessages.length + 1);
        updatedMessages[currentMessages.length] = newMessage;
        foundChat.setMessage(updatedMessages);
        chatRepository.save(foundChat);

        return ChatResponse.builder().status(true).message(aiResponse).id(requestBody.getId()).build();
    }

    @Transactional
    public ChatResponse deleteChat(String id) {
        logger.info("Deleting chat with id: " + id);
        Optional<Chat> chat = chatRepository.findByConversationId(id);
        if (chat.isEmpty()) {
            throw new NotFoundException("Chat not found");
        }
        Chat foundChat = chat.get();
        chatRepository.delete(foundChat);

        return ChatResponse.builder().status(true).message("Chat deleted").id(foundChat.getConversationId()).build();
    }

    private CompletableFuture<String> sendChatRequest(String userMessage) {
        String openaiApiKey = environment.getProperty("OPENAI_API_KEY");
        String openaiOrganizationId = environment.getProperty("OPENAI_ORG_ID");
        String formattedUserMessage = userMessage.replaceAll("\"", "\\\\\"");
        String requestBody = "{\"model\": \"gpt-4\", \"messages\": ["
                + "{\"role\": \"user\", \"content\": \"" + formattedUserMessage + "\"}"
                + "]}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("OpenAI-Organization", openaiOrganizationId)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }
}
