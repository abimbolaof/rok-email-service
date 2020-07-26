package com.sonofiroko.email;

import com.sonofiroko.email.service.ApplicationContextHolder;
import com.sonofiroko.email.service.SQSManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MainApplication {

    private static final String accessKeyId = "AKIAJFAYLLNC675CV53A";
    private static final String secretAccessKey = "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";

    public static void main (String... args) {
        System.setProperty("aws.accessKeyId", accessKeyId);
        System.setProperty("aws.secretKey", secretAccessKey);

        SpringApplication.run(MainApplication.class, args);

        ApplicationContext ctx = ApplicationContextHolder.getCtx();
        SQSManager sqsManager = ctx.getBean(SQSManager.class);
        sqsManager.start();
    }
}
