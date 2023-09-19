package com.example.server.controllers;

import com.example.server.dto.requests.SaveChatRequest;
import com.example.server.dto.requests.UpdateChatRequest;
import com.example.server.dto.responses.ChatResponse;
import com.example.server.services.ChatService;
import com.example.server.dto.requests.SendChatMessageRequest;
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
    public ResponseEntity<List<Chat>> getAllChats(@PathVariable String userId) {
        return new ResponseEntity<>(chatService.getAllChats(userId), null, 200);
    }

    @PostMapping
    public ResponseEntity<Chat> saveChatWithId(@RequestBody SaveChatRequest saveChatRequest) {
        return new ResponseEntity<>(chatService.saveChatWithId(saveChatRequest), null, 201);
    }

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> updateChatMessage(@RequestBody SendChatMessageRequest sendChatMessageRequest) {
        return new ResponseEntity<>(chatService.sendChatMessage(sendChatMessageRequest), null, 200);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ChatResponse> deleteChat(@PathVariable String id) {
        return new ResponseEntity<>(chatService.deleteChat(id), null, 200);
    }

    @PutMapping
    public ResponseEntity<ChatResponse> updateChat(@RequestBody UpdateChatRequest chat) {
        return new ResponseEntity<>(chatService.updateChat(chat), null, 200);
    }
}