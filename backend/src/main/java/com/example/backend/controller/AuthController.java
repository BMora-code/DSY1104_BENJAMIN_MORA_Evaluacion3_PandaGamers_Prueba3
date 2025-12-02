package com.example.backend.controller;

import com.example.backend.dto.AuthRequest;
import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.security.JwtUtil;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Registro de nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = User.class),
                            examples = @ExampleObject(value = "{ \"username\": \"juan\", \"email\": \"juan@example.com\" }")))
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User u = userService.register(req.getUsername(), req.getEmail(), req.getPassword());
            return ResponseEntity.ok(u);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Login de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación correcta",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = "{ \"token\": \"eyJhbGci...\", \"username\": \"juan\" }")))
    })
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody AuthRequest req) {
                String loginId = req.getUsername();

                try {
                        // UserService.findByUsername now handles email lookup first
                        User user = userService.findByUsername(loginId).orElse(null);
                        if (user == null) {
                                return ResponseEntity.status(401).body("Invalid credentials");
                        }

                        // Use the actual username from the found user for authentication
                        String authUsername = user.getUsername();

                        authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(authUsername, req.getPassword())
                        );

                        String token = jwtUtil.generateToken(authUsername);

                        // Include roles and email in response
                        java.util.Set<String> roleNames = new java.util.HashSet<>();
                        String userEmail = "";
                        if (user.getRoles() != null) {
                                user.getRoles().forEach(r -> roleNames.add(r.name()));
                        }
                        userEmail = user.getEmail();

                        return ResponseEntity.ok(new AuthResponse(token, authUsername, userEmail, roleNames));
                } catch (Exception e) {
                        return ResponseEntity.status(401).body("Invalid credentials");
                }
        }
}
