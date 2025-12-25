package com.deneme.demo.service;

import com.deneme.demo.model.Todo;
import com.deneme.demo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.net.HttpURLConnection;
import java.net.URL;
// ------------------

import com.deneme.demo.model.Todo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
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
    void checkExternalAccess_ExampleCom_ShouldReturn200() throws Exception {
        // Given
        URL url = new URL("https://example.com");
        
        // When
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        
        int responseCode = connection.getResponseCode();
        
        // Then
        assertEquals(400, responseCode, "Example.com sitesine erişilemiyor!");
        
        // Temizlik
        connection.disconnect();
    }
    
    @Test
    void getAllTodos_ShouldReturnAllTodos() {
        // Given
        List<Todo> expectedTodos = Arrays.asList(todo1, todo2);
        when(todoRepository.findAll()).thenReturn(expectedTodos);

        // When
        List<Todo> actualTodos = todoService.getAllTodos();

        // Then
        assertEquals(2, actualTodos.size());
        assertEquals(expectedTodos, actualTodos);
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    void getTodoById_WhenTodoExists_ShouldReturnTodo() {
        // Given
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));

        // When
        Optional<Todo> result = todoService.getTodoById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(todo1.getTitle(), result.get().getTitle());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    void getTodoById_WhenTodoNotExists_ShouldReturnEmpty() {
        // Given
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Todo> result = todoService.getTodoById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(todoRepository, times(1)).findById(999L);
    }

    @Test
    void createTodo_ShouldSaveAndReturnTodo() {
        // Given
        Todo newTodo = new Todo("Yeni Todo", "Yeni Açıklama", false);
        when(todoRepository.save(any(Todo.class))).thenReturn(newTodo);

        // When
        Todo savedTodo = todoService.createTodo(newTodo);

        // Then
        assertNotNull(savedTodo);
        assertEquals("Yeni Todo", savedTodo.getTitle());
        verify(todoRepository, times(1)).save(newTodo);
    }

    @Test
    void updateTodo_WhenTodoExists_ShouldUpdateAndReturnTodo() {
        // Given
        Todo updatedDetails = new Todo("Güncellenmiş Başlık", "Güncellenmiş Açıklama", true);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        // When
        Todo result = todoService.updateTodo(1L, updatedDetails);

        // Then
        assertNotNull(result);
        assertEquals("Güncellenmiş Başlık", result.getTitle());
        assertEquals("Güncellenmiş Açıklama", result.getDescription());
        assertTrue(result.isCompleted());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void updateTodo_WhenTodoNotExists_ShouldThrowException() {
        // Given
        Todo updatedDetails = new Todo("Güncellenmiş Başlık", "Güncellenmiş Açıklama", true);
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            todoService.updateTodo(999L, updatedDetails);
        });
        verify(todoRepository, times(1)).findById(999L);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void deleteTodo_ShouldCallRepositoryDelete() {
        // Given
        Long todoId = 1L;
        doNothing().when(todoRepository).deleteById(todoId);

        // When
        todoService.deleteTodo(todoId);

        // Then
        verify(todoRepository, times(1)).deleteById(todoId);
    }

    @Test
    void getCompletedTodos_ShouldReturnOnlyCompletedTodos() {
        // Given
        List<Todo> completedTodos = Arrays.asList(todo2);
        when(todoRepository.findByCompleted(true)).thenReturn(completedTodos);

        // When
        List<Todo> result = todoService.getCompletedTodos();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
        verify(todoRepository, times(1)).findByCompleted(true);
    }

    @Test
    void getPendingTodos_ShouldReturnOnlyPendingTodos() {
        // Given
        List<Todo> pendingTodos = Arrays.asList(todo1);
        when(todoRepository.findByCompleted(false)).thenReturn(pendingTodos);

        // When
        List<Todo> result = todoService.getPendingTodos();

        // Then
        assertEquals(1, result.size());
        assertFalse(result.get(0).isCompleted());
        verify(todoRepository, times(1)).findByCompleted(false);
    }

    @Test
    void toggleTodoStatus_WhenTodoExists_ShouldToggleStatus() {
        // Given
        boolean originalStatus = todo1.isCompleted();
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo1);

        // When
        Todo result = todoService.toggleTodoStatus(1L);

        // Then
        assertNotNull(result);
        assertEquals(!originalStatus, result.isCompleted());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(todo1);
    }

    @Test
    void toggleTodoStatus_WhenTodoNotExists_ShouldThrowException() {
        // Given
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            todoService.toggleTodoStatus(999L);
        });
        verify(todoRepository, times(1)).findById(999L);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void createTodo_WithNullTitle_ShouldStillSave() {
        // Given
        Todo todoWithNullTitle = new Todo(null, "Açıklama", false);
        when(todoRepository.save(any(Todo.class))).thenReturn(todoWithNullTitle);

        // When
        Todo result = todoService.createTodo(todoWithNullTitle);

        // Then
        assertNotNull(result);
        assertNull(result.getTitle());
        verify(todoRepository, times(1)).save(todoWithNullTitle);
    }

    @Test
    void getAllTodos_WhenNoTodos_ShouldReturnEmptyList() {
        // Given
        when(todoRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Todo> result = todoService.getAllTodos();

        // Then
        assertTrue(result.isEmpty());
        verify(todoRepository, times(1)).findAll();
    }
}

