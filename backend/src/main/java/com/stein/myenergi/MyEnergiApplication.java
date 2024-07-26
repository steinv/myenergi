package com.stein.myenergi;

import com.google.firebase.FirebaseApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableScheduling
public class MyEnergiApplication {

    public static void main(String[] args) {
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
//                .setDatabaseUrl(dbUrl)
//                .build();
        FirebaseApp.initializeApp();
        SpringApplication.run(MyEnergiApplication.class, args);
    }

}
