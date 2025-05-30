package com.wojet.pmtool.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wojet.pmtool.model.User;
import com.wojet.pmtool.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    public User registerUser(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password); 
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        // Client client = clientRepository.findByName("Dama Trade");
        // user.setClient(client);
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return userOpt;
            }
        }
        return Optional.empty();
    }
}
