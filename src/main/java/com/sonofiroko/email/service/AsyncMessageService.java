package com.sonofiroko.email.service;

import com.sonofiroko.email.model.ApiException;
import com.sonofiroko.email.model.EmailMessage;
import com.sonofiroko.email.types.MessageCallback;
import com.sonofiroko.email.types.MessageFormat;
import com.sonofiroko.email.types.MessageTemplateType;
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
public class AsyncMessageService {

    private final BlockingQueue<Runnable> jobQueue;
    private final ExecutorService executorService;

    @Value("${service.message.address.from}")
	private String fromAddress;

	@Autowired
	@Qualifier("emailMessageService")
	private MessageService<EmailMessage> emailMessageService;

	@Autowired
	@Qualifier("SMSMessageService")
	private MessageService<EmailMessage> smsMessageService;

	static Logger logger = LoggerFactory.getLogger(AsyncMessageService.class.getName());

    @Autowired
    public AsyncMessageService(@Value("${service.messaging.job.queue_size}") int queueSize,
                               @Value("${service.messaging.max_pool_size}") int maxPoolSize){
        jobQueue = new ArrayBlockingQueue<>(queueSize);
        executorService =
                new ThreadPoolExecutor(2, maxPoolSize, 10, TimeUnit.SECONDS, jobQueue);
    }

    public void process(EmailMessage msg, final MessageCallback callback) {
		executorService.execute(() -> {
			try {
				MessageTemplateType templateType = MessageTemplateType.fromName(msg.getTemplateName());
				msg.setSubject(templateType.getTitle());
				msg.setFrom(fromAddress);
				MessageTemplateProvider.newInstance()
						.setValues(msg.getValues())
						.apply(msg, templateType);
				sendMessage(msg);
				callback.call(true);
			} catch (ApiException e) {
				e.printStackTrace();
				callback.call(false);
			}
		});
	}

	private void sendMessage(EmailMessage object) {
		if (object.getMessageFormat().equals(MessageFormat.EMAIL)) {
			try {
				emailMessageService.send(object);
				logger.debug("Message Sent.");
			} catch (Exception mx) {
				logger.error(mx.getMessage());
			}
		} else if (object.getMessageFormat().equals(MessageFormat.SMS)) {
			try {
				smsMessageService.send(object);
				logger.debug("Message Sent.");
			} catch (Exception mx) {
				logger.error(mx.getMessage());
			}
		}
	}
}
