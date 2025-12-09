package com.deneme.demo.controller;

import com.deneme.demo.model.Todo;
import com.deneme.demo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    private Todo todo1;
    private Todo todo2;

    @BeforeEach
    void setUp() {
        todo1 = new Todo("Test Todo 1", "Açıklama 1", false);
        todo1.setId(1L);

        todo2 = new Todo("Test Todo 2", "Açıklama 2", true);
        todo2.setId(2L);
    }

    @Test
    void getAllTodos_ShouldReturnTodoList() throws Exception {
        // Given
        List<Todo> todos = Arrays.asList(todo1, todo2);
        when(todoService.getAllTodos()).thenReturn(todos);

        // When & Then
        mockMvc.perform(get("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Todo 1")))
                .andExpect(jsonPath("$[0].completed", is(false)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Test Todo 2")))
                .andExpect(jsonPath("$[1].completed", is(true)));

        verify(todoService, times(1)).getAllTodos();
    }

    @Test
    void getTodoById_WhenTodoExists_ShouldReturnTodo() throws Exception {
        // Given
        when(todoService.getTodoById(1L)).thenReturn(Optional.of(todo1));

        // When & Then
        mockMvc.perform(get("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Todo 1")))
                .andExpect(jsonPath("$.description", is("Açıklama 1")))
                .andExpect(jsonPath("$.completed", is(false)));

        verify(todoService, times(1)).getTodoById(1L);
    }

    @Test
    void getTodoById_WhenTodoNotExists_ShouldReturn404() throws Exception {
        // Given
        when(todoService.getTodoById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/todos/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).getTodoById(999L);
    }

    @Test
    void createTodo_ShouldReturnCreatedTodo() throws Exception {
        // Given
        Todo newTodo = new Todo("Yeni Todo", "Yeni Açıklama", false);
        Todo savedTodo = new Todo("Yeni Todo", "Yeni Açıklama", false);
        savedTodo.setId(3L);

        when(todoService.createTodo(any(Todo.class))).thenReturn(savedTodo);

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTodo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("Yeni Todo")))
                .andExpect(jsonPath("$.description", is("Yeni Açıklama")))
                .andExpect(jsonPath("$.completed", is(false)));

        verify(todoService, times(1)).createTodo(any(Todo.class));
    }

    @Test
    void updateTodo_WhenTodoExists_ShouldReturnUpdatedTodo() throws Exception {
        // Given
        Todo updatedTodo = new Todo("Güncellenmiş Todo", "Güncellenmiş Açıklama", true);
        updatedTodo.setId(1L);

        when(todoService.updateTodo(eq(1L), any(Todo.class))).thenReturn(updatedTodo);

        // When & Then
        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Güncellenmiş Todo")))
                .andExpect(jsonPath("$.description", is("Güncellenmiş Açıklama")))
                .andExpect(jsonPath("$.completed", is(true)));

        verify(todoService, times(1)).updateTodo(eq(1L), any(Todo.class));
    }

    @Test
    void updateTodo_WhenTodoNotExists_ShouldReturn404() throws Exception {
        // Given
        Todo updatedTodo = new Todo("Güncellenmiş Todo", "Güncellenmiş Açıklama", true);
        when(todoService.updateTodo(eq(999L), any(Todo.class)))
                .thenThrow(new RuntimeException("Todo bulunamadı"));

        // When & Then
        mockMvc.perform(put("/api/todos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).updateTodo(eq(999L), any(Todo.class));
    }

    @Test
    void deleteTodo_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(todoService).deleteTodo(1L);

        // When & Then
        mockMvc.perform(delete("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).deleteTodo(1L);
    }

    @Test
    void getCompletedTodos_ShouldReturnOnlyCompletedTodos() throws Exception {
        // Given
        List<Todo> completedTodos = Arrays.asList(todo2);
        when(todoService.getCompletedTodos()).thenReturn(completedTodos);

        // When & Then
        mockMvc.perform(get("/api/todos/completed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].completed", is(true)));

        verify(todoService, times(1)).getCompletedTodos();
    }

    @Test
    void getPendingTodos_ShouldReturnOnlyPendingTodos() throws Exception {
        // Given
        List<Todo> pendingTodos = Arrays.asList(todo1);
        when(todoService.getPendingTodos()).thenReturn(pendingTodos);

        // When & Then
        mockMvc.perform(get("/api/todos/pending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].completed", is(false)));

        verify(todoService, times(1)).getPendingTodos();
    }

    @Test
    void toggleTodoStatus_WhenTodoExists_ShouldReturnToggledTodo() throws Exception {
        // Given
        Todo toggledTodo = new Todo("Test Todo 1", "Açıklama 1", true);
        toggledTodo.setId(1L);

        when(todoService.toggleTodoStatus(1L)).thenReturn(toggledTodo);

        // When & Then
        mockMvc.perform(patch("/api/todos/1/toggle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.completed", is(true)));

        verify(todoService, times(1)).toggleTodoStatus(1L);
    }

    @Test
    void toggleTodoStatus_WhenTodoNotExists_ShouldReturn404() throws Exception {
        // Given
        when(todoService.toggleTodoStatus(999L))
                .thenThrow(new RuntimeException("Todo bulunamadı"));

        // When & Then
        mockMvc.perform(patch("/api/todos/999/toggle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).toggleTodoStatus(999L);
    }

    @Test
    void getAllTodos_WhenNoTodos_ShouldReturnEmptyList() throws Exception {
        // Given
        when(todoService.getAllTodos()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(todoService, times(1)).getAllTodos();
    }

    @Test
    void createTodo_WithInvalidData_ShouldStillProcessRequest() throws Exception {
        // Given
        Todo invalidTodo = new Todo(null, null, false);
        Todo savedTodo = new Todo(null, null, false);
        savedTodo.setId(4L);

        when(todoService.createTodo(any(Todo.class))).thenReturn(savedTodo);

        // When & Then
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTodo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)));

        verify(todoService, times(1)).createTodo(any(Todo.class));
    }
}

