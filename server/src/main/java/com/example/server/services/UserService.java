package com.example.server.services;

import com.example.server.dto.responses.UserResponse;
import com.example.server.models.User;
import com.example.server.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final Logger logger = Logger.getLogger(ChatService.class.getName());

    @Transactional
    @PreAuthorize("#id == authentication.principal.id")
    public UserResponse getUserById(String id) {
        logger.info("Getting user with id: " + id);
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().toString())
                .apiKey(user.getApiKey())
                .build();
    }
}
