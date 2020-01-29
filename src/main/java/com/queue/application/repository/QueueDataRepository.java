package com.queue.application.repository;

import com.queue.application.domain.QueueData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface QueueDataRepository extends JpaRepository<QueueData,String> {
    long countByQueueName(@PathVariable String queueName);
    QueueData findTopByQueueNameOrderByForwardDesc(@PathVariable String queueName);
    QueueData findTopByQueueNameOrderByForwardAsc(@PathVariable String queuename);
    List<QueueData> findByQueueName(@PathVariable String queueName);
}
