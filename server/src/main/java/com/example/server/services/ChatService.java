package com.example.server.services;

import com.example.server.dto.requests.GptRequest;
import com.example.server.dto.requests.SaveChatRequest;
import com.example.server.dto.requests.UpdateChatRequest;
import com.example.server.dto.responses.ChatResponse;
import com.example.server.models.GptMessage;
import com.example.server.models.Message;
import com.example.server.dto.requests.SendChatMessageRequest;
import com.example.server.exceptions.NotFoundException;
import com.example.server.models.Chat;
import com.example.server.repositories.ChatRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
    public ChatResponse sendChatMessage(SendChatMessageRequest requestBody) {
        logger.info("Updating chat with id: " + requestBody.getId());
        Optional<Chat> chat = chatRepository.findByConversationId(requestBody.getId());
        String userMessage = requestBody.getMessage();
        if (chat.isEmpty()) {
            throw new NotFoundException("Chat not found");
        }
        Chat foundChat = chat.get();
        List<GptMessage> conversationHistory = new ArrayList<>();
        if (foundChat.getMessage() != null) {
            for (Message message : foundChat.getMessage()) {
                conversationHistory.add(GptMessage.builder().role("user").content(message.getMessage()).build());
                conversationHistory.add(GptMessage.builder().role("assistant").content(message.getResponse()).build());
            }
        }
        conversationHistory.add(GptMessage.builder().role("user").content(userMessage).build());
        GptRequest gptRequest = GptRequest.builder()
                .model("gpt-4")
                .temperature(0.7f)
                .messages(conversationHistory)
                .build();
        Mono<String> openAIResponse = sendChatRequest(gptRequest);
        JsonNode jsonResponse;

        try {
            jsonResponse = new ObjectMapper().readTree(openAIResponse.block());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing OpenAI response");
        }
        Message newMessage = new Message(requestBody.getMessage(), jsonResponse.get("choices").get(0).get("message").get("content").asText());

        Message[] currentMessages = foundChat.getMessage();
        if (currentMessages == null) {
            currentMessages = new Message[0];
        }
        Message[] updatedMessages = Arrays.copyOf(currentMessages, currentMessages.length + 1);
        updatedMessages[currentMessages.length] = newMessage;
        foundChat.setMessage(updatedMessages);
        chatRepository.save(foundChat);

        return ChatResponse.builder().status(true).message(newMessage.getResponse()).id(requestBody.getId()).build();
    }

    @Transactional
    public ChatResponse updateChat(UpdateChatRequest requestBody) {
        logger.info("Updating chat with id: " + requestBody.getConversationId());
        Optional<Chat> chat = chatRepository.findByConversationId(requestBody.getConversationId());
        if (chat.isEmpty()) {
            throw new NotFoundException("Chat not found");
        }
        Chat foundChat = chat.get();
        foundChat.setAlias(requestBody.getAlias());
        chatRepository.save(foundChat);

        return ChatResponse.builder().status(true).message("Chat updated").id(requestBody.getConversationId()).build();
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

    private Mono<String> sendChatRequest(GptRequest requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + environment.getProperty("OPENAI_API_KEY"));
        headers.set("OpenAI-Organization", environment.getProperty("OPENAI_ORG_ID"));

        String url = "https://api.openai.com/v1/chat/completions";

        WebClient webClient = WebClient.create();

        return webClient.post()
                .uri(url)
                .headers(h -> h.addAll(headers))
                .body(Mono.just(requestBody), GptRequest.class)
                .retrieve()
                .bodyToMono(String.class);
    }
}
