package br.com.yasmimsilva.todolist.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.yasmimsilva.todolist.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
}
