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
	
	@ApiOperation(value = "key값에 따라 특정 Partition으로 message를 전송")
	@PostMapping("/partition/{userid}/{key}")
	public ResponseEntity<String> sendUserMessage(@PathVariable(value = "userid") String userId, @PathVariable(value="key") String key, @RequestBody String message) throws Exception{
		logger.info("---------------------------------------");
		logger.info("Send message to kafka:"+message);
		latch = new CountDownLatch(messagesPerRequest);
		
		if (!StringUtils.isEmpty(userId) && !StringUtils.isEmpty(message)) {
	        IntStream.range(0, messagesPerRequest)
        	.forEach(i -> this.kafkaTemplate.send(topicName + userId, key,  message + String.valueOf(i))
        			);
		} else {
			return ResponseEntity.ok(NO_MESSAGE);
		}
		return ResponseEntity.ok(SUCCCESS);
	}	
	
	@ApiOperation(value = "round robin 방식으로 message를 전송")
	@PostMapping("/round-robin/{userid}")
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
