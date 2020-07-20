package com.sonofiroko.email.service;

import com.sonofiroko.email.model.Message;
import com.sonofiroko.email.types.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * Created By: Olusegun Abimbola Aug 26, 2017
 *
 **/
@Service
@Scope("singleton")
public class AsyncMessageService<K extends Message> {

    private final BlockingQueue<Runnable> jobQueue;
    private final ExecutorService executorService;

	@Autowired
	@Qualifier("emailMessageService")
	private MessageService<Message> emailMessageService;

	@Autowired
	@Qualifier("SMSMessageService")
	private MessageService<Message> smsMessageService;

	static Logger logger = LoggerFactory.getLogger(AsyncMessageService.class.getName());

    @Autowired
    public AsyncMessageService(@Value("${service.messaging.job.queue_size}") int queueSize,
                               @Value("${service.messaging.max_pool_size}") int maxPoolSize){
        jobQueue = new ArrayBlockingQueue<Runnable>(queueSize);
        executorService =
                new ThreadPoolExecutor(2, maxPoolSize, 10, TimeUnit.SECONDS, jobQueue);
    }

	public void send(K object) {
        executorService.execute(() -> {
            sendMessage(object);
        });
	}

	private void sendMessage(K object) {
		if (object.getMessageFormat().equals(MessageFormat.EMAIL)) {
			try {
				emailMessageService.send(object);
				logger.info("Message Sent.");
			} catch (Exception mx) {
				logger.error(mx.getMessage());
			}
		} else if (object.getMessageFormat().equals(MessageFormat.SMS)) {
			try {
				smsMessageService.send(object);
				logger.info("Message Sent.");
			} catch (Exception mx) {
				logger.error(mx.getMessage());
			}
		}
	}
}
