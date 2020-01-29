package com.queue.application.service;


import com.queue.application.domain.DefaultQueue;
import com.queue.application.domain.QueueData;

import java.util.List;

public interface QueueServices {
    DefaultQueue createQueue(DefaultQueue defaultQueue);

    DefaultQueue SaveMessage(DefaultQueue queue);

    List<DefaultQueue> getMessage();

    DefaultQueue DeleteUserQueue(String UserId);

    void DeleteAllQueues();

    QueueData AddNewMessage(QueueData queueData,String userId);

    QueueData getFirstmessage(String userId);

    QueueData PollQueue(String userId);

    List<QueueData> getAllMessages(String userId);
}
