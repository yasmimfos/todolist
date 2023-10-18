package br.com.yasmimsilva.todolist.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.yasmimsilva.todolist.domain.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByIdUser(UUID idUser);
}
