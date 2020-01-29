package com.queue.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queue.application.domain.DefaultQueue;
import com.queue.application.domain.QueueData;
import lombok.extern.slf4j.Slf4j;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.springframework.web.reactive.function.BodyInserters;

import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ApplicationTests extends AbstractQueueTest {
	//Integration Test
	@Before
	public void setup(){
		setUpDataBase();
	}

	@Test
	public void TestQueueE2E() throws Exception{

		LOGGER.info("---------------------------------------------------");
		LOGGER.info("Test is running for User Queue");
		LOGGER.info("---------------------------------------------------");

		createQueue();
		shouldSaveMessage();
		shouldpeekMessageFromUserQueue();
		shouldpollMessagesFromUserQueue();
	}

	@Test
	public void TestQueueDefaultUser() throws Exception {

		LOGGER.info("---------------------------------------------------");
		LOGGER.info("Test is running for Default  Queue");
		LOGGER.info("---------------------------------------------------");

		shouldSaveMessageinDefault();
		shouldpeekMessageFromDefaultQueue();
		shouldpollMessagesFromDefaultQueue();

	}

	@After
	public void afterSetUp(){
		setUpDataBase();
	}

}




@Slf4j
abstract class AbstractQueueTest {
	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private ObjectMapper objectMapper;

	private DefaultQueue defaultQueue = new DefaultQueue("user","queue");

	private QueueData queueData = new QueueData(UUID.randomUUID(),"Message-1",defaultQueue);

	private QueueData queueDatadefault = new QueueData("Default Message");


	public void setUpDataBase(){
		this.webTestClient.delete().uri("/queue/deleteAll").exchange().expectStatus().isOk();
	}

	public void createQueue() throws Exception{
		this.webTestClient.post().uri("/queue/create").body(BodyInserters.fromObject(defaultQueue)).exchange().expectStatus().isCreated();
	}

	public void shouldSaveMessage() throws  Exception{
		this.webTestClient.post().uri("/queue/message/user").body(BodyInserters.fromObject(queueData)).exchange().expectStatus().isCreated();
	}

	public void shouldSaveMessageinDefault() throws  Exception{
		this.webTestClient.post().uri("/queue/message").body(BodyInserters.fromObject(queueDatadefault)).exchange().expectStatus().isCreated();
	}


	public void shouldpeekMessageFromUserQueue(){
		this.webTestClient.get().uri("/queue/peek/user").exchange().expectStatus().isOk();
	}

	public void shouldpeekMessageFromDefaultQueue(){
		this.webTestClient.get().uri("/queue/peek").exchange().expectStatus().isOk();
	}



	public void shouldpollMessagesFromUserQueue(){
		this.webTestClient.delete().uri("/queue/poll/user").exchange().expectStatus().isOk();
	}

	public void shouldpollMessagesFromDefaultQueue(){
		this.webTestClient.delete().uri("/queue/poll").exchange().expectStatus().isOk();
	}

}

