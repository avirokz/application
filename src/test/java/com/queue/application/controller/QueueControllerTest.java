package com.queue.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queue.application.domain.DefaultQueue;
import com.queue.application.domain.QueueData;
import com.queue.application.service.QueueServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(value = QueueController.class,secure=false)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class QueueControllerTest {
    @MockBean
    private QueueServices queueServices;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    public void shouldcreateQueue() throws Exception{
        DefaultQueue defaultQueue = new DefaultQueue("tarun","queue");

        String Json = objectMapper.writeValueAsString(defaultQueue);

        when(queueServices.createQueue(defaultQueue)).thenReturn(defaultQueue);


        MockHttpServletResponse mockHttpServletResponse = mockMvc.perform(post("/queue/create")
        .content(Json).contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(mockHttpServletResponse.getStatus(),is(HttpStatus.CREATED.value()));

        verify(queueServices).createQueue(defaultQueue);
    }

    @Test
    public void getListOfQueues() throws Exception{
        DefaultQueue defaultQueue = new DefaultQueue("tarun","queue");
        DefaultQueue defaultQueue1 = new DefaultQueue("septa","queue1");

        List<DefaultQueue> list = new ArrayList<>();

        list.add(defaultQueue);
        list.add(defaultQueue1);

        when(queueServices.getMessage()).thenReturn(list);

        MockHttpServletResponse response = mockMvc.perform(get("/queue/listusers")).andReturn().getResponse();

        assertThat(response.getStatus(),is(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(),is(objectMapper.writeValueAsString(queueServices.getMessage())));
    }

    @Test
    public void deleteQueue() throws Exception{
        DefaultQueue defaultQueue = new DefaultQueue("tarun","queue");

        when(queueServices.DeleteUserQueue("tarun")).thenReturn(defaultQueue);

        MockHttpServletResponse response = mockMvc.perform(delete("/queue/delete/tarun")).andReturn().getResponse();

        verify(queueServices).DeleteUserQueue("tarun");
        assertThat(response.getStatus(),is(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(),is(objectMapper.writeValueAsString(queueServices.DeleteUserQueue("tarun"))));
    }

    @Test
    public void deleteAll() throws Exception{
        DefaultQueue defaultQueue = new DefaultQueue("u1","q1");
        DefaultQueue defaultQueue1 = new DefaultQueue("u2","q2");

        doNothing().when(queueServices).DeleteAllQueues();

        MockHttpServletResponse response = mockMvc.perform(delete("/queue/deleteAll")).andReturn().getResponse();

        verify(queueServices).DeleteAllQueues();
        assertThat(response.getStatus(),is(HttpStatus.OK.value()));
    }
    @Test
    public void shouldcreateMessagForUser() throws Exception{
        DefaultQueue defaultQueue = new DefaultQueue("tarun","queue");

        QueueData queueData = new QueueData(UUID.randomUUID(),"this is a message",defaultQueue);

        when(queueServices.AddNewMessage(queueData,"tarun")).thenReturn(queueData);

        MockHttpServletResponse response = mockMvc.perform(post("/queue/message/tarun").content(objectMapper.writeValueAsString(queueData))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(response.getStatus(),is(HttpStatus.CREATED.value()));
        assertThat(response.getContentAsString(),is(objectMapper.writeValueAsString(queueData)));
        verify(queueServices).AddNewMessage(queueData,"tarun");
    }

    @Test
    public void shouldCreateMessageDefaultUser() throws  Exception{
        DefaultQueue defaultQueue = new DefaultQueue("tarun","queue");

        QueueData queueData = new QueueData(UUID.randomUUID(),"default user Message",defaultQueue);

        when(queueServices.AddNewMessage(queueData,"default")).thenReturn(queueData);


        MockHttpServletResponse response = mockMvc.perform(post("/queue/message").content(objectMapper.writeValueAsString(queueData))
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertThat(response.getStatus(),is(HttpStatus.CREATED.value()));
        assertThat(response.getContentAsString(),is(objectMapper.writeValueAsString(queueData)));
        verify(queueServices).AddNewMessage(queueData,"default");

    }
    @Test
    public void shouldpeekUserQueue() throws Exception{

        DefaultQueue defaultQueue = new DefaultQueue("tarun","queue");

        QueueData queueData = new QueueData(UUID.randomUUID(),"peek message",defaultQueue);

        when(queueServices.getFirstmessage("tarun")).thenReturn(queueData);

        MockHttpServletResponse response = mockMvc.perform(get("/queue/peek/tarun")).andReturn().getResponse();

        assertThat(response.getStatus(),is(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(),is(objectMapper.writeValueAsString(queueData)));
        verify(queueServices).getFirstmessage("tarun");
    }

    @Test
    public void shouldpollUserQueue() throws Exception{
        DefaultQueue defaultQueue = new DefaultQueue("tarun","queue");
        QueueData queueData = new QueueData(UUID.randomUUID(),"poll message",defaultQueue);

        when(queueServices.PollQueue("tarun")).thenReturn(queueData);

        MockHttpServletResponse response = mockMvc.perform(delete("/queue/poll/tarun")).andReturn().getResponse();

        assertThat(response.getStatus(),is(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(),is(objectMapper.writeValueAsString(queueData)));
        verify(queueServices).PollQueue("tarun");
    }

    @Test
    public void shouldGetListOfMessages() throws Exception{
        DefaultQueue defaultQueue = new DefaultQueue("tarun","queue");

        QueueData queueData = new QueueData(UUID.randomUUID(),"message-1",defaultQueue);
        QueueData  queueData1 = new QueueData(UUID.randomUUID(),"message-2",defaultQueue);
        QueueData queueData2 = new QueueData(UUID.randomUUID(),"message-3",defaultQueue);

        List<QueueData> list = new ArrayList<>();
        list.add(queueData);
        list.add(queueData1);
        list.add(queueData2);

        when(queueServices.getAllMessages("tarun")).thenReturn(list);

        MockHttpServletResponse response = mockMvc.perform(get("/queue/list/tarun")).andReturn().getResponse();

        assertThat(response.getStatus(),is(HttpStatus.OK.value()));
        assertThat(response.getContentAsString(),is(objectMapper.writeValueAsString(list)));
        verify(queueServices).getAllMessages("tarun");

    }
}
