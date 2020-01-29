package com.queue.application.repository;

import com.queue.application.domain.DefaultQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PathVariable;

public interface QueueReository extends JpaRepository<DefaultQueue,String> {
        DefaultQueue findByQueueName(@PathVariable(name = "queue_name") String queueName);
        DefaultQueue findByUserId(@PathVariable(name = "userid") String userid);
        DefaultQueue findByUserIdOrQueueName(@PathVariable(name = "userid") String userid,@PathVariable(name = "queue_name") String queueName);
}
