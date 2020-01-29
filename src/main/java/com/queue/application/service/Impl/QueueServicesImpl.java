package com.queue.application.service.Impl;

import com.queue.application.domain.QueueData;
import com.queue.application.exception.QueueException;
import com.queue.application.exception.QueueMaxCountException;
import com.queue.application.repository.QueueDataRepository;
import com.queue.application.repository.QueueReository;
import com.queue.application.service.QueueServices;
import com.queue.application.domain.DefaultQueue;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class QueueServicesImpl implements QueueServices {
    private QueueReository queueReository;
    private QueueDataRepository queueDataRepository;

    @Value("${queue.count}")
    private long queueCount;

    @Value("${queue.maxsize}")
    private long queueSize;

    public QueueServicesImpl(QueueReository queueReository,QueueDataRepository queueDataRepository){
        this.queueReository = queueReository;
        this.queueDataRepository = queueDataRepository;
    }

    @Override
    public DefaultQueue createQueue(DefaultQueue defaultQueue) {
        long count = queueReository.count();
        if(count == queueCount && count != 0){
            throw new QueueMaxCountException("Queue Max Count Reached.");
        }
        else{
            DefaultQueue existQueue = queueReository.findByUserIdOrQueueName(defaultQueue.getUserId(),defaultQueue.getQueueName());
           if(existQueue != null){
               throw new QueueException("Queue Alreay Exist with this username or queue");
           }
           else{
             return queueReository.save(defaultQueue);
           }
        }
    }

    @Override
    public DefaultQueue SaveMessage(DefaultQueue queue) {
        DefaultQueue q = queueReository.saveAndFlush(queue);
       return q;
    }

    @Override
    public List<DefaultQueue> getMessage() {
        return queueReository.findAll();
    }

    @Override
    public DefaultQueue DeleteUserQueue(String UserId) {
        DefaultQueue defaultQueue = queueReository.findByUserId(UserId);
        if(defaultQueue!=null){
             queueReository.delete(defaultQueue);
             return defaultQueue;
        }
        else {
            throw new QueueException("Queue not exist for Deletion");
        }
    }

    @Override
    public void DeleteAllQueues() {
            queueReository.deleteAll();
    }

    @Override
    public QueueData AddNewMessage(QueueData queueData, String userId) {
        DefaultQueue defaultQueue = queueReository.findByUserId(userId);
        if(defaultQueue != null){
           return saveMessageinQueueWithForward(defaultQueue,queueData);
        }
        else {
                DefaultQueue defaultQueue1 = new DefaultQueue("default","default");
                queueReository.saveAndFlush(defaultQueue1);
                return saveMessageinQueueWithForward(defaultQueue1,queueData);
        }
    }

    @Override
    public QueueData getFirstmessage(String userID) {
        String queueName = queueReository.findByUserId(userID).getQueueName();
        if(queueName == null){
            throw new QueueException("Your Queue is Empty");
        }
        else {
            return queueDataRepository.findTopByQueueNameOrderByForwardAsc(queueName);
        }
    }

    @Override
    public QueueData PollQueue(String userId) {
        String queueName = queueReository.findByUserId(userId).getQueueName();
        if(queueName == null){
            throw new QueueException("Your Queue is Empty");
        }
        else {
            QueueData queueData = queueDataRepository.findTopByQueueNameOrderByForwardAsc(queueName);
            queueDataRepository.delete(queueData);
            return queueData;
        }

    }

    @Override
    public List<QueueData> getAllMessages(String userId) {
        String queueName = queueReository.findByUserId(userId).getQueueName();
        if(queueName != null){
            return  queueDataRepository.findByQueueName(queueName);
        }
        else {
            throw new QueueException("Your Queue is Empty");
        }
    }

    private QueueData saveMessageinQueueWithForward(DefaultQueue defaultQueue,QueueData queueData){
        String queueName = defaultQueue.getQueueName();
        long count = queueDataRepository.countByQueueName(queueName);
        if(count == queueCount){
            throw new QueueException("Messgae Count Limit Reached.");
        }
        else {
            queueData.setDefaultQueue(defaultQueue);
            queueData.setQueueName(queueName);
            queueData.setForward(count + 1);
            return  queueDataRepository.save(queueData);
        }
    }
}
