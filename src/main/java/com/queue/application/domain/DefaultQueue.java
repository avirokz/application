package com.queue.application.domain;


import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "DefaultQueue")
public class DefaultQueue {

    @Column(nullable = false,unique = true)
    private String userId;

    @Id
    @Column(nullable = false)
    private String queueName;

    public DefaultQueue(){
    }

    public DefaultQueue(String userID,String queueName){
        this.queueName = queueName;
        this.userId = userID;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "{userName}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultQueue th = (DefaultQueue) o;
        return Objects.equals(userId,th.userId) && Objects.equals(queueName,th.queueName);
    }
}
