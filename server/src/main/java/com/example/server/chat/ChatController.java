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

    @GetMapping
    public List<Chat> getAllChats() {
        return chatService.getAllChats();
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