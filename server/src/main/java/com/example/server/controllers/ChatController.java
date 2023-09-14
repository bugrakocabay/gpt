package com.example.server.controllers;

import com.example.server.dto.requests.SaveChatRequest;
import com.example.server.dto.responses.ChatResponse;
import com.example.server.services.ChatService;
import com.example.server.dto.requests.UpdateChatRequest;
import com.example.server.models.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/id/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable String id) {
        return new ResponseEntity<>(chatService.getChatById(id), null, 200);
    }

    @GetMapping("/{userId}")
    public List<Chat> getAllChats(@PathVariable String userId) {
        return chatService.getAllChats(userId);
    }

    @PostMapping
    public Chat saveChatWithId(@RequestBody SaveChatRequest saveChatRequest) {
        return chatService.saveChatWithId(saveChatRequest);
    }

    @PostMapping("/message")
    public ChatResponse updateChatMessage(@RequestBody UpdateChatRequest updateChatRequest) {
        return chatService.updateChatMessage(updateChatRequest);
    }

    @DeleteMapping("/{id}")
    public ChatResponse deleteChat(@PathVariable String id) {
        return chatService.deleteChat(id);
    }
}