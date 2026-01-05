package com.example.saga.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.saga.model.Task;
import com.example.saga.model.TaskSearchResponse;

import reactor.core.publisher.Mono;

@Service
public class TaskService {
	
	@Autowired
	WebClient webClient;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	public void processTask(String taskId) {
			ResponseCookie  sessionCookie = webClient.post()
		            .uri("http://localhost:8080/api/login")
		            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		            .bodyValue("username=demo&password=demo")
		            .exchangeToMono(response -> Mono.just(response.cookies().getFirst("OPERATE-SESSION")))
		            .block();

		        // Step 2: Assign the Task (Fixes the 400 "Task is not assigned" error)
		        webClient.patch()
		            .uri("http://localhost:8080/v1/tasks/{taskId}/assign", taskId)
		            .cookie("OPERATE-SESSION", sessionCookie.getValue())
		            .contentType(MediaType.APPLICATION_JSON)
		            .bodyValue(Map.of("assignee", "demo", "allowOverrideAssignment", true))
		            .retrieve()
		            .bodyToMono(Void.class)
		            .block();

		        // Step 3: Complete the Task
		        webClient.patch()
		            .uri("http://localhost:8080/v1/tasks/{taskId}/complete", taskId)
		            .cookie("OPERATE-SESSION", sessionCookie.getValue())
		            .contentType(MediaType.APPLICATION_JSON)
		            .bodyValue(Map.of("variables", List.of(
		                Map.of("name", "approved", "value", "true")
		            )))
		            .retrieve()
		            .bodyToMono(Void.class)
		            .block();
		    }


	public String getTaskId(TaskRequestDTO requestDTO) {
		List<Map<String,Object>> task=null;
		
		ResponseCookie  sessionCookie = webClient.post()
	            .uri("http://localhost:8080/api/login")
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .bodyValue("username=demo&password=demo")
	            .exchangeToMono(response -> Mono.just(response.cookies().getFirst("OPERATE-SESSION")))
	            .block();
		
		
		
		int maxRetries = 15; // 15 seconds
        int delayMs = 1000;
        for (int i = 0; i < maxRetries; i++) {

             List<Map<String,Object>> response =
                    webClient.post()
                            .uri("http://localhost:8080/v1/tasks/search")
                            .cookie("OPERATE-SESSION", sessionCookie.getValue())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(requestDTO)
                            .retrieve()
                            .bodyToMono(List.class)
                            .block();

            if (!response.isEmpty()
               ) {

                return response
                        .get(0)
                        .get("id")
                        .toString();
            }

            // wait before retry
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        
		

//		task= webClient.post()
//	            .uri("http://localhost:8080/v1/tasks/search")
//	            .cookie("OPERATE-SESSION", sessionCookie.getValue())
//	            .contentType(MediaType.APPLICATION_JSON)
//	            .bodyValue(requestDTO)
//	            .retrieve()
//	            .bodyToMono(List.class)
//	            .block();
//		}

//		String taskId=!task.isEmpty()?task.get(0).get("id").toString():"";
//		return taskId;
	}
        return "";
		
		
	}
}

