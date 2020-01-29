package com.queue.application.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Queuedata")
public class QueueData {

    @Id
    private UUID id = UUID.randomUUID();


    private long forward;

    @Column(name = "queue_name",insertable = false,updatable = false)
    private String queueName;



    private String message;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_name",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DefaultQueue defaultQueue;

    public QueueData(UUID id,String message,DefaultQueue defaultQueue){
        //this.queueName = queuename;
        this.id = id;
        this.message = message;
        this.defaultQueue = defaultQueue;
    }

    public QueueData(String message){
        this.message = message;
    }

    public QueueData(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }


    public DefaultQueue getDefaultQueue() {
        return defaultQueue;
    }

    public void setDefaultQueue(DefaultQueue defaultQueue) {
            this.defaultQueue = defaultQueue;
    }

    public long getForward() {
        return forward;
    }

    public void setForward(long forward) {
        this.forward = forward;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(forward);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==this) return true;
        if(obj==null||getClass()!=obj.getClass()) return false;
        QueueData q = (QueueData) obj;
        return Objects.equals(q.forward,forward) && Objects.equals(q.defaultQueue,defaultQueue);
    }
}
