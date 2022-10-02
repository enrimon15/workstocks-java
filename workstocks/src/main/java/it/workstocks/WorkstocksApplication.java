package it.workstocks;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@SpringBootApplication
public class WorkstocksApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(WorkstocksApplication.class, args);
	}

//	@Bean
//	FirebaseMessaging firebaseMessaging() throws IOException {
//
//		GoogleCredentials googleCredentials = GoogleCredentials
//				.fromStream(new ClassPathResource("workstocks-android-ultimo-firebase-adminsdk-g2bi9-023815c875.json")
//						.getInputStream());
//		FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(googleCredentials).build();
//		FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-app");
//		return FirebaseMessaging.getInstance(app);
//
//	}

}
