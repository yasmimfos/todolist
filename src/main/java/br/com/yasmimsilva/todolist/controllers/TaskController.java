package br.com.yasmimsilva.todolist.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.yasmimsilva.todolist.Repository.TaskRepository;
import br.com.yasmimsilva.todolist.domain.Task;
import br.com.yasmimsilva.todolist.utils.utils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody Task taskModel, HttpServletRequest request) {
        Object idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início e/ou de término não pode ser maior que a data atual");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de início não pode ser maior que a data de término");
        }

        Task task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(200).body(task);
    }

    @GetMapping("/")
    public List<Task> list(HttpServletRequest request) {
        Object idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody Task taskModel, HttpServletRequest request, @PathVariable UUID id) {
        Task task = this.taskRepository.findById(id).orElse(null);
        Object idUser = request.getAttribute("idUser");

        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tarefa não encontrada.");
        }

        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Sem permissão para alterar essa tarefa.");
        }

        utils.copyNonNullProperties(taskModel, task);

        Task taskUpdated = this.taskRepository.save(task);
        return ResponseEntity.ok().body(taskUpdated);
    }

}
