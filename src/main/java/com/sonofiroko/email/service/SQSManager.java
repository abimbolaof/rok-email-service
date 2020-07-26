package com.sonofiroko.email.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.sonofiroko.email.model.dto.PostMessage;
import com.sonofiroko.email.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SQSManager {

    private static volatile boolean PROCESS_MSGS = true;
    private static volatile boolean isStarted = false;

    @Value("${sqs.queue.email}")
    private String emailQueueUrl;

    @Autowired
    private AsyncMessageService service;

    public void start(){
        new Thread(()->{
            isStarted = true;
            AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
            List<Message> messages = null;

            while (PROCESS_MSGS) {
                messages = sqs.receiveMessage(emailQueueUrl).getMessages();
                if (messages != null && messages.size() > 0) {
                    for (Message m : messages){
                        System.out.println(m.getBody());
                        try {
                            PostMessage postMessage = Objects.fromJSON(m.getBody(), PostMessage.class);
                            service.process(postMessage);
                            sqs.deleteMessage(emailQueueUrl, m.getReceiptHandle());
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            isStarted = false;
        }).start();
    }

    public void stop() {
        PROCESS_MSGS = false;
        while (isStarted){

        };
    }
}
