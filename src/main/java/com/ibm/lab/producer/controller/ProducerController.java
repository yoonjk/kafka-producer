package com.ibm.lab.producer.controller;

import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value="/api/producer")
public class ProducerController {
	Logger logger = LoggerFactory.getLogger(ProducerController.class);
	
	@Value("${kafka.topic-name}")
	private String topicName;
	
	private final int messagesPerRequest;
	
	private CountDownLatch latch;
	
	private String DEFAULT_KEY = "ibm";
	
	private final String SUCCCESS = "Send successful";
	
	private final String NO_MESSAGE = "Please Input Message!";
	
	private final KafkaTemplate kafkaTemplate;

	public ProducerController(KafkaTemplate kafkaTemplate,
			@Value("${kafka.message-per-request}") final int messagesPerRequest) {
		this.kafkaTemplate = kafkaTemplate;
		this.messagesPerRequest = messagesPerRequest;
	}
	
	@PostMapping("/partition")
	public ResponseEntity<String> sendMessage(@RequestBody String message) throws Exception{
		logger.info("---------------------------------------");
		logger.info("Before send message to kafka:"+message);
		latch = new CountDownLatch(messagesPerRequest);
		
		
		if (!StringUtils.isEmpty(message)) {
	        IntStream.range(0, messagesPerRequest)
	        	.forEach(i -> this.kafkaTemplate.send(topicName, message,
	        			 message + i)
	        );
		
	        logger.info("Send successful:"+message);
		}
		return ResponseEntity.ok(SUCCCESS);
	}
	
	
	
	@ApiOperation(value = "사용가 생성한 topic에 message를 Put.")
	@PutMapping("/partition/{userid}")
	public ResponseEntity<String> sendUserMessage(@PathVariable(value = "userid") String userId, @RequestBody String message) throws Exception{
		logger.info("---------------------------------------");
		logger.info("Before send message to kafka:"+message);
		latch = new CountDownLatch(messagesPerRequest);
		
		if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(message)) {
	        IntStream.range(0, messagesPerRequest)
        	.forEach(i -> this.kafkaTemplate.send(topicName + userId, message,  message + String.valueOf(i))
        			);
		} else {
			return ResponseEntity.ok(NO_MESSAGE);
		}
		return ResponseEntity.ok(SUCCCESS);
	}
	
	
	@PostMapping("/round-robin")
	public ResponseEntity<String> sendRoundRobinMessage(@RequestBody String message) throws Exception{
		logger.info("---------------------------------------");
		logger.info("Send message to kafka:"+message);
		latch = new CountDownLatch(messagesPerRequest);
		
		if (!StringUtils.isEmpty(message)) {
	        IntStream.range(0, messagesPerRequest)
        	.forEach(i -> this.kafkaTemplate.send(topicName, message + String.valueOf(i))
        			);
		} else {
			return ResponseEntity.ok(NO_MESSAGE);
		}

		return ResponseEntity.ok(SUCCCESS);
	}
	
	@PutMapping("/round-robin/{userid}")
	public ResponseEntity<String> sendRoundRobinUserMessage(@PathVariable(value = "userid") String userId, @RequestBody String message) throws Exception{
		logger.info("---------------------------------------");
		logger.info("Send message to kafka:"+message);
		latch = new CountDownLatch(messagesPerRequest);
		
		if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(message)) {
	        IntStream.range(0, messagesPerRequest)
        	.forEach(i -> this.kafkaTemplate.send(topicName + userId, message + String.valueOf(i))
        			);
		} else {
			return ResponseEntity.ok(NO_MESSAGE);
		}

		return ResponseEntity.ok(SUCCCESS);
	}	
}
