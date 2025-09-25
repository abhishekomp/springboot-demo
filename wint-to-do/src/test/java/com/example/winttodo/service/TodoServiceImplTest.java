package com.example.winttodo.service;

import com.example.winttodo.dto.TodoResponse;
import com.example.winttodo.model.TodoEntity;
import com.example.winttodo.repository.TodoRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    // A test method for findAllByArchivedFalse should return a paginated list of non-archived todos
    // You can use Mockito to mock the repository call and return a sample Page<TodoEntity>
    // Then verify that the service method returns the expected Page<TodoResponse>
    // You can also verify that the repository method was called with the correct Pageable argument
    // Use assertions to check the size and content of the returned Page<TodoResponse>
    // You can use PageImpl to create a sample Page<TodoEntity> for testing
    // Example:
    // Page<TodoEntity> todoEntityPage = new PageImpl<>(List.of(sampleTodoEntity1, sampleTodoEntity2), pageable, totalElements);
    // when(todoRepository.findAllByArchivedFalse(pageable)).thenReturn(todoEntityPage);
    // Page<TodoResponse> result = todoService.findAllByArchivedFalse(pageable);
    @Test
    void test_shouldReturnPaginatedNonArchivedTodos() {

        // -----Arrange-----
        Pageable pageable = PageRequest.of(0, 5);
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setId(1L);
        todoEntity.setTitle("Sample Todo");
        todoEntity.setArchived(false);
        /*TodoEntity todoEntity2 = new TodoEntity();
        todoEntity2.setId(2L);
        todoEntity2.setTitle("Sample Todo 2");
        todoEntity2.setArchived(true);*/

        // Mock the repository call to return a sample Page<TodoEntity> using Mockito
        // Using traditional Mockito style
        // Stubs the repository method to return a Page containing one todoEntity when called with the given pageable.
        // The '1' argument specifies the total number of elements in the Page (for pagination metadata).
        when(todoRepository.findAllByArchivedFalse(pageable))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(todoEntity), pageable, 1));

        // Using BDDMockito style
        // Stubs the repository method to return a Page containing one todoEntity when called with the given pageable.
        // The '1' argument specifies the total number of elements in the Page (for pagination metadata).
        given(todoRepository.findAllByArchivedFalse(pageable))
                .willReturn(new org.springframework.data.domain.PageImpl<>(java.util.List.of(todoEntity), pageable, 1));

        // -----Act-----
        Page<TodoResponse> allByArchivedFalse = todoService.findAllByArchivedFalse(pageable);

        // -----Assert-----
        assertNotNull(allByArchivedFalse);
        assertEquals(1, allByArchivedFalse.getTotalElements());
        assertEquals(1, allByArchivedFalse.getContent().size());
        assertEquals("Sample Todo", allByArchivedFalse.getContent().get(0).getTitle());

        //assertJ style assertions
//        assertThat(allByArchivedFalse.getContent())
//                .hasSize(1)
//                .extracting(TodoResponse::getTitle)
//                .containsExactly("Sample Todo");

        // assertJ style with multiple fields
        assertThat(allByArchivedFalse.getContent())
                .hasSize(1)
                .extracting(TodoResponse::getTitle, TodoResponse::getId)
                .containsExactly(tuple("Sample Todo", 1L));


        // Verify that the repository method was called with the correct Pageable argument
        verify(todoRepository, times(1)).findAllByArchivedFalse(pageable);
        // Verify that the repository method was called at most once not more than once and not less than once
        verify(todoRepository, atMost(1)).findAllByArchivedFalse(pageable);
        // Verify that the repository method was called at least once not more than once with an instance of Pageable
        verify(todoRepository, atLeast(1)).findAllByArchivedFalse(any(Pageable.class));
        // Verify that the repository method was called at least once not more than once with any Pageable
        verify(todoRepository).findAllByArchivedFalse(any(Pageable.class));
        // Verify that no other interactions happened with the repository
        verifyNoMoreInteractions(todoRepository);
    }


    // A test method for createTodo should verify that a new todo is created and returned as TodoResponse
    // You can use Mockito to mock the repository save call and return a sample TodoEntity
    // Then verify that the service method returns the expected TodoResponse
    // You can also verify that the repository save method was called with a TodoEntity that has the same properties as the input TodoRequest
    // Use assertions to check the properties of the returned TodoResponse
    // Example:
    // TodoEntity savedEntity = new TodoEntity();
    // savedEntity.setId(1L);
    // savedEntity.setTitle(todoRequest.getTitle());
    // when(todoRepository.save(any(TodoEntity.class))).thenReturn(savedEntity);
    // TodoResponse result = todoService.createTodo(todoRequest);
    // assertEquals(1L, result.getId());

    @Test
    @Disabled("Not implemented yet")
    void createTodo() {
        assert false;
    }

    @Test
    @Disabled("Not implemented yet")
    void getAll() {
        assert false;
    }
}