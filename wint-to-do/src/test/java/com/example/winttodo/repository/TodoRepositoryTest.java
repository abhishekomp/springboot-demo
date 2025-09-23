package com.example.winttodo.repository;

import com.example.winttodo.WintToDoApplication;
import com.example.winttodo.model.TodoEntity;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.sql.DataSource;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TodoRepositoryTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    void printDataSourceUrl() throws Exception {
        System.out.printf(">>>>>>>>>>DataSource URL: %s%n", dataSource.getConnection().getMetaData().getURL());
        assertNotNull(dataSource);
        assertThat(dataSource.getConnection().getMetaData().getURL()).contains("jdbc:h2:mem");
    }

    @Test
    void should_returnOnlyNonArchivedTodos_whenFindAllByArchivedFalseIsCalled() {
        // Given we save some todos
        TodoEntity todo1 = new TodoEntity();
        todo1.setTitle("Active Todo");
        todo1.setArchived(false);
        todoRepository.save(todo1);
        TodoEntity todo2 = new TodoEntity();
        todo2.setTitle("Archived Todo");
        todo2.setArchived(true);
        todoRepository.save(todo2);
        // When we call findAllByArchivedFalse
        Page<TodoEntity> page = todoRepository.findAllByArchivedFalse(PageRequest.of(0, 5));

        List<TodoEntity> todoList = page.getContent();
        log.info("Non-archived todos: {}", todoList);
        // Then we should only get non-archived todos
        // Hamcrest style assertion
        assertThat(page.getTotalElements(), org.hamcrest.Matchers.is(1L));
        assertThat(todoList.size(), org.hamcrest.Matchers.is(1));

        // JUnit style assertion
        assertEquals(1L, page.getTotalElements());
        assertEquals(1, todoList.size());


        // AssertJ style assertion
        assertThat(page.getTotalElements()).isEqualTo(1L);
        assertThat(page.getContent()).allMatch(todo -> !todo.isArchived());
        assertThat(page.getContent()).extracting(TodoEntity::getTitle).containsExactlyInAnyOrder("Active Todo");
    }

    @Test
    void should_returnZeroTodos_whenAllTodosAreArchived() {
        // Given we save some archived todos
        TodoEntity todo1 = new TodoEntity();
        todo1.setTitle("Archived Todo 1");
        todo1.setArchived(true);
        todoRepository.save(todo1);
        TodoEntity todo2 = new TodoEntity();
        todo2.setTitle("Archived Todo 2");
        todo2.setArchived(true);
        todoRepository.save(todo2);
        // When we call findAllByArchivedFalse
        Page<TodoEntity> page = todoRepository.findAllByArchivedFalse(PageRequest.of(0, 5));

        List<TodoEntity> todoList = page.getContent();
        // Then we should get zero non-archived todos
        // JUnit style assertion
        assertEquals(0L, page.getTotalElements());
        assertEquals(0, todoList.size());

        // AssertJ style assertion
        assertThat(page.getTotalElements()).isEqualTo(0L);
        assertThat(page.getContent()).isEmpty();

        // Hamcrest style assertion
        assertThat(page.getTotalElements(), org.hamcrest.Matchers.is(0L));
        assertThat(todoList.size(), org.hamcrest.Matchers.is(0));
    }

    @Test
    void should_returnNonArchivedTodosWithMultiplePages_whenManyNonArchivedTodosExist() {
        // Given we save some todos as archived and non-archived
        for (int i = 1; i <= 3; i++) {
            TodoEntity todo = new TodoEntity();
            todo.setTitle("Archived Todo " + i);
            todo.setArchived(true);
            todoRepository.save(todo);
        }
        for (int i = 1; i <= 12; i++) {
            TodoEntity todo = new TodoEntity();
            todo.setTitle("Active Todo " + i);
            todo.setArchived(false);
            todoRepository.save(todo);
        }
        // When we call findAllByArchivedFalse with page size 5
        Page<TodoEntity> page1 = todoRepository.findAllByArchivedFalse(PageRequest.of(0, 5));
        Page<TodoEntity> page2 = todoRepository.findAllByArchivedFalse(PageRequest.of(1, 5));
        Page<TodoEntity> page3 = todoRepository.findAllByArchivedFalse(PageRequest.of(2, 5));

        // Then we should get multiple pages of non-archived todos
        assertEquals(12L, page1.getTotalElements());
        assertEquals(3, page1.getTotalPages());
        assertFalse(page3.isEmpty());

        // assertJ style assertions
        assertThat(page3.getTotalElements()).isEqualTo(12L);
        assertThat(page3.getTotalPages()).isEqualTo(3);
        assertThat(page3.isEmpty()).isFalse();
        assertThat(page1.isLast()).isFalse();
        assertThat(page1.isFirst()).isTrue();

        // page 2 will have a next page
        assertTrue(page2.hasNext());
        // page 3 will not have a next page
        assertFalse(page3.hasNext());
        // page 2 will have a previous page
        assertTrue(page2.hasPrevious());
        // page 1 will not have a previous page
        assertFalse(page1.hasPrevious());

        assertEquals(5, page1.getContent().size());
        assertEquals(5, page2.getContent().size());
        assertEquals(2, page3.getContent().size());

        // AssertJ style assertion for verifying the items in the last page
        assertThat(page3.getContent()).extracting(TodoEntity::getTitle)
                .containsExactlyInAnyOrder("Active Todo 11", "Active Todo 12");
    }
}