package com.example.todo.todobackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.example.todo.todobackend.controller.TaskController;
import com.example.todo.todobackend.repository.TaskRepository;
import com.example.todo.todobackend.service.TaskService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TodoBackendApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Test
	void contextLoads() {
		// Verify that the application context loads successfully
		assertNotNull(context);
	}
	
	@Test
	void controllerBeanExists() {
		// Verify that the TaskController bean exists
		assertTrue(context.containsBean("taskController"));
		assertNotNull(context.getBean(TaskController.class));
	}
	
	@Test
	void serviceBeanExists() {
		// Verify that the TaskService bean exists
		assertTrue(context.containsBean("taskService"));
		assertNotNull(context.getBean(TaskService.class));
	}
	
	@Test
	void repositoryBeanExists() {
		// Verify that the TaskRepository bean exists
		assertNotNull(context.getBean(TaskRepository.class));
	}
	
	// No need for additional application startup test as contextLoads() is sufficient
}