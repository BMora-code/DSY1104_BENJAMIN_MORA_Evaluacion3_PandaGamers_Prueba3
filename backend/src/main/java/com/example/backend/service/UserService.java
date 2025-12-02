package com.example.backend.service;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String username, String email, String password) {
        // Check if user already exists by username or email
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        User u = new User(username, email, passwordEncoder.encode(password));
        HashSet<Role> roles = new HashSet<>();
        roles.add(Role.CLIENTE);
        u.setRoles(roles);
        return userRepository.save(u);
    }

    public Optional<User> findByUsername(String username) {
        // First try to find by email (since frontend allows login with email)
        Optional<User> userByEmail = userRepository.findByEmail(username);
        if (userByEmail.isPresent()) {
            return userByEmail;
        }
        // If not found by email, try by username
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(String username, String email, String password, Role role) {
        // Check if user already exists by username or email
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        User u = new User(username, email, passwordEncoder.encode(password));
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        u.setRoles(roles);
        return userRepository.save(u);
    }

    public User updateUser(String id, String username, String email, Role role) {
        User existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if username/email conflicts with other users
        if (!existing.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        if (!existing.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        existing.setUsername(username);
        existing.setEmail(email);
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        existing.setRoles(roles);
        return userRepository.save(existing);
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
