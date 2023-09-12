package com.example.server.chat;

import com.example.server.user.User;
import com.example.server.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
    @Autowired
    private UserRepository userRepository;
    private final Environment environment;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Logger logger = Logger.getLogger(ChatService.class.getName());

    public ChatService(Environment environment) {
        this.environment = environment;
    }

    @Transactional
    public Chat saveChatWithId(ChatDto chatDto) {
        logger.info("Saving chat with id: " + chatDto.getId());
        Chat chat = new Chat();
        chat.setConversationId(chatDto.getId());
        User user = userRepository.findById(chatDto.getUserId()).get();
        String[] userChats = user.getChats();
        if (userChats == null) {
            userChats = new String[0];
        }
        String[] updatedChats = Arrays.copyOf(userChats, userChats.length + 1);
        updatedChats[userChats.length] = chatDto.getId();
        user.setChats(updatedChats);
        userRepository.save(user);
        return chatRepository.save(chat);
    }

    @Transactional
    public List<Chat> getAllChats() {
        logger.info("Getting all chats");
        return chatRepository.findAll();
    }

    @Transactional
    public ChatResponseDto updateChatMessage(UpdateChatDto requestBody) {
        logger.info("Updating chat with id: " + requestBody.getId());
        Chat chat = chatRepository.findByConversationId(requestBody.getId());
        String userMessage = requestBody.getMessage();

        String openAIResponse = sendChatRequest(userMessage).join();
        JsonNode jsonResponse;
        try {
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

            ChatResponseDto chatResponseDto = new ChatResponseDto();
            chatResponseDto.setId(requestBody.getId());
            chatResponseDto.setMessage(aiResponse);
            return chatResponseDto;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<String> sendChatRequest(String userMessage) {
        String openaiApiKey = environment.getProperty("OPENAI_API_KEY");
        String openaiOrganizationId = environment.getProperty("OPENAI_ORG_ID");
        String requestBody = "{\"model\": \"gpt-4\", \"messages\": ["
                + "{\"role\": \"user\", \"content\": \"" + userMessage + "\"}"
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

    public ChatResponseDto deleteChat(String id) {
        logger.info("Deleting chat with id: " + id);
        Chat chat = chatRepository.findByConversationId(id);
        ChatResponseDto chatResponseDto = new ChatResponseDto();
        if (chat == null) {
            chatResponseDto.setId(id);
            chatResponseDto.setMessage("Chat not found");
            chatResponseDto.setStatus(false);
            return chatResponseDto;
        }
        chatRepository.delete(chat);
        chatResponseDto.setId(id);
        chatResponseDto.setMessage("OK");
        chatResponseDto.setStatus(true);
        return chatResponseDto;
    }
}
