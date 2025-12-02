package com.example.backend.controller;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Operation(summary = "Listar usuarios", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Listado de usuarios")})
    @GetMapping
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @Operation(summary = "Obtener usuario por id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable String id) {
        return ResponseEntity.ok(userRepository.findById(id).orElse(null));
    }

    @Operation(summary = "Eliminar usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Crear usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado")
    })
    @PostMapping
    public ResponseEntity<User> create(@RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String email = userData.get("email");
        String password = userData.get("password");
        String roleStr = userData.get("role");

        Role role = "admin".equals(roleStr) ? Role.ADMIN : Role.CLIENTE;

        User user = userService.createUser(username, email, password, role);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Actualizar usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody Map<String, String> userData) {
        String username = userData.get("username");
        String email = userData.get("email");
        String roleStr = userData.get("role");

        Role role = "admin".equals(roleStr) ? Role.ADMIN : Role.CLIENTE;

        User user = userService.updateUser(id, username, email, role);
        return ResponseEntity.ok(user);
    }
}
