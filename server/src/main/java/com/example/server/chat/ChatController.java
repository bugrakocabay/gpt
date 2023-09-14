package com.example.server.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/id/{id}")
    public Chat getChatById(@PathVariable String id) {
        return chatService.getChatById(id);
    }

    @GetMapping("/{userId}")
    public List<Chat> getAllChats(@PathVariable String userId) {
        return chatService.getAllChats(userId);
    }

    @PostMapping
    public Chat saveChatWithId(@RequestBody ChatDto chatDto) {
        return chatService.saveChatWithId(chatDto);
    }

    @PostMapping("/message")
    public ChatResponseDto updateChatMessage(@RequestBody UpdateChatDto updateChatDto) {
        return chatService.updateChatMessage(updateChatDto);
    }

    @DeleteMapping("/{id}")
    public ChatResponseDto deleteChat(@PathVariable String id) {
        return chatService.deleteChat(id);
    }
}