package com.example.server.services;

import com.example.server.dto.requests.SaveChatRequest;
import com.example.server.dto.responses.ChatResponse;
import com.example.server.models.Message;
import com.example.server.dto.requests.UpdateChatRequest;
import com.example.server.exceptions.NotFoundException;
import com.example.server.models.Chat;
import com.example.server.repositories.ChatRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

        // Attempt to find the chat by ID
        Optional<Chat> chatOptional = chatRepository.findById(id);

        if (chatOptional.isPresent()) {
            return chatOptional.get();
        } else {
            throw new NotFoundException("Chat with ID " + id + " not found");
        }
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
    public ChatResponse updateChatMessage(UpdateChatRequest requestBody) {
        logger.info("Updating chat with id: " + requestBody.getId());
        try {
            Chat chat = chatRepository.findByConversationId(requestBody.getId());
            String userMessage = requestBody.getMessage();

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
            Message[] currentMessages = chat.getMessage();
            if (currentMessages == null) {
                currentMessages = new Message[0];
            }
            Message[] updatedMessages = Arrays.copyOf(currentMessages, currentMessages.length + 1);
            updatedMessages[currentMessages.length] = newMessage;
            chat.setMessage(updatedMessages);
            chatRepository.save(chat);

            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setId(requestBody.getId());
            chatResponse.setMessage(aiResponse);
            return chatResponse;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public ChatResponse deleteChat(String id) {
        logger.info("Deleting chat with id: " + id);
        Chat chat = chatRepository.findByConversationId(id);
        ChatResponse chatResponse = new ChatResponse();
        if (chat == null) {
            chatResponse.setId(id);
            chatResponse.setMessage("Chat not found");
            chatResponse.setStatus(false);
            return chatResponse;
        }
        chatRepository.delete(chat);
        chatResponse.setId(id);
        chatResponse.setMessage("OK");
        chatResponse.setStatus(true);
        return chatResponse;
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
