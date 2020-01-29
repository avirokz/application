package com.queue.application.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.queue.application.domain.DefaultQueue;
import com.queue.application.domain.QueueData;
import com.queue.application.exception.QueueException;
import com.queue.application.exception.QueueMaxCountException;
import com.queue.application.repository.QueueDataRepository;
import com.queue.application.repository.QueueReository;
import com.queue.application.service.Impl.QueueServicesImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;


public class QueueServiceImplTest {
    private QueueReository queueReository;
    private QueueDataRepository queueDataRepository;
    private QueueServices queueServices;
    private DefaultQueue defaultQueue;
    private QueueData queueData;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup(){
        queueReository = mock(QueueReository.class);
        queueDataRepository = mock(QueueDataRepository.class);
        queueServices =  spy(new QueueServicesImpl(queueReository,queueDataRepository));
        ReflectionTestUtils.setField(queueServices,"queueCount",10);
        ReflectionTestUtils.setField(queueServices,"queueSize",10);
        defaultQueue = new DefaultQueue("tarun","queue-1");
        queueData = new QueueData(UUID.randomUUID(), "Message", defaultQueue);

    }

    @Test
    public void shouldCheckMaxQueueException() {
        long lt = 10;
        when(queueReository.count()).thenReturn(lt);

        try {
            queueServices.createQueue(defaultQueue);
            fail("should have thrown a Queue Max Count Reached Exception");
        } catch (QueueMaxCountException q) {
            assertThat(q.getMessage().contains("Queue Max Count Reached."), is(true));
        }
    }

    @Test
    public void shouldCheckQueueExist() throws Exception{
        when(queueReository.findByUserIdOrQueueName("tarun","queue-1")).thenReturn(defaultQueue);

        try{
            queueServices.createQueue(defaultQueue);
            fail("should have thrown Queue Exception");
        }
        catch (QueueException q){
            assertThat(q.getMessage().contains("Queue Alreay Exist with this username or queue"),is(true));
        }

        }

    @Test
    public void shouldSaveNewQueue() throws Exception{

        queueServices.createQueue(defaultQueue);
        ArgumentCaptor<DefaultQueue> argumentCaptor = ArgumentCaptor.forClass(DefaultQueue.class);
        verify(queueReository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getQueueName(),is("queue-1"));
    }

    @Test
    public void getListOfQueues(){
        DefaultQueue defaultQueue1 = new DefaultQueue("tarun-2","queue-2");

        List<DefaultQueue> list = Arrays.asList(defaultQueue,defaultQueue1);

        when(queueReository.findAll()).thenReturn(list);

        List<DefaultQueue> getList = queueServices.getMessage();

        verify(queueReository).findAll();

        assertThat(getList,is(list));
    }

    @Test
    public void deleteUserQueue(){

        when(queueReository.findByUserId("tarun")).thenReturn(defaultQueue);

        DefaultQueue deletedQueue = queueServices.DeleteUserQueue("tarun");

        verify(queueServices).DeleteUserQueue("tarun");
        assertThat(deletedQueue,is(defaultQueue));
    }

    @Test
    void ShouldSaveMessageForExistUser() {
        when(queueReository.findByUserId("tarun")).thenReturn(defaultQueue);


        when(queueDataRepository.save(queueData)).thenReturn(queueData);


        QueueData queueData1 = queueServices.AddNewMessage(queueData,"tarun");

        ArgumentCaptor<QueueData> argumentCaptor = ArgumentCaptor.forClass(QueueData.class);

        verify(queueDataRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getDefaultQueue(),is(defaultQueue));

        assertThat(argumentCaptor.getValue().getMessage(),is("Message"));

        assertThat(queueData1,is(queueData));

    }

    @Test
    void ShouldSaveIfUserNotExistInDeffaultQueue(){
        QueueData queueData = new QueueData("Message");

        when(queueDataRepository.save(queueData)).thenReturn(queueData);

        QueueData queueData1 = queueServices.AddNewMessage(queueData,"default");

        verify(queueReository).saveAndFlush(new DefaultQueue("default","default"));

        ArgumentCaptor<QueueData> argumentCaptor = ArgumentCaptor.forClass(QueueData.class);

        verify(queueDataRepository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getMessage(),is("Message"));

        assertThat(queueData1,is(queueData));
    }

    @Test
    void QueueShouldThrowExceptionInCaseMaxMessageCountReached(){
        long maxMessage = 10;
        when(queueReository.findByUserId("tarun")).thenReturn(defaultQueue);
        when(queueDataRepository.countByQueueName(defaultQueue.getQueueName())).thenReturn(maxMessage);

        QueueData queueData = new QueueData(UUID.randomUUID(),"Message",defaultQueue);
        try{
            queueServices.AddNewMessage(queueData,"tarun");
            fail("Should Have Thrown Exception Max Message Count Reached");
        }
        catch (QueueException q){
            assertThat(q.getMessage().contains("Messgae Count Limit Reached."),is(true));
        }

    }


    @Test
    void QueueShouldPoll(){
        when(queueReository.findByUserId("tarun")).thenReturn(defaultQueue);
        when(queueDataRepository.findTopByQueueNameOrderByForwardAsc("queue-1")).thenReturn(queueData);

        QueueData queueData1 = queueServices.PollQueue("tarun");

        assertThat(queueData1,is(queueData));
    }

    @Test
    void QueueShouldPeek(){
        when(queueReository.findByUserId("tarun")).thenReturn(defaultQueue);
        when(queueDataRepository.findTopByQueueNameOrderByForwardAsc("queue-1")).thenReturn(queueData);


       assertThat(queueServices.getFirstmessage("tarun").getMessage(),is("Message"));

       verify(queueDataRepository).findTopByQueueNameOrderByForwardAsc("queue-1");
    }

}
