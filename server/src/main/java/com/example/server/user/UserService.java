package com.example.server.user;

import com.example.server.chat.ChatService;
import com.example.server.config.JwtService;
import com.example.server.exception.DuplicateException;
import com.example.server.exception.PasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final Logger logger = Logger.getLogger(ChatService.class.getName());

    @Transactional
    public UserResponseDto saveUser(UserDto user) throws Exception {
        try {
            logger.info("Saving user with username: " + user.getUsername());
            String hashedPassword = hashPassword(user.getPassword());
            User newUser = User.builder()
                    .username(user.getUsername())
                    .password(hashedPassword)
                    .role(Role.USER)
                    .build();
            User savedUser = userRepository.save(newUser);
            return new UserResponseDto(savedUser.getId(), savedUser.getUsername(), Role.USER);
        } catch (Exception e) {
            logger.warning("Error saving user: " + e);
            if (e instanceof DuplicateKeyException) {
                throw new DuplicateException("User already exists");
            }
            return null;
        }
    }

    @Transactional
    public LoginResponseDto login(UserDto user) throws Exception {
        try {
            logger.info("Logging in user with username: " + user.getUsername());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            User foundUser = userRepository.findByUsername(user.getUsername());
            String jwtToken = jwtService.generateToken(foundUser);

            return new LoginResponseDto(foundUser.getId(),foundUser.getUsername(), Role.USER, jwtToken);
        } catch (Exception e) {
            logger.warning("Error logging in user: " + e);
            if (e instanceof BadCredentialsException || e instanceof InternalAuthenticationServiceException) {
                throw new PasswordException("Incorrect credentials");
            }
            throw new Exception(e.getMessage());
        }
    }

    public String hashPassword(String plainPassword) {
        return bCryptPasswordEncoder.encode(plainPassword);
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return bCryptPasswordEncoder.matches(plainPassword, hashedPassword);
    }
}
