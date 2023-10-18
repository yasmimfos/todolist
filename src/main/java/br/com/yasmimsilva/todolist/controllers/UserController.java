package br.com.yasmimsilva.todolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.yasmimsilva.todolist.Repository.UserRepository;
import br.com.yasmimsilva.todolist.domain.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity create(@RequestBody User user) {

        if (this.userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(400).body("Usuário já existe");
        }

        String passHashred = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
        user.setPassword(passHashred);

        User userCreated = this.userRepository.save(user);
        return ResponseEntity.status(200).body(userCreated);

    }
}
