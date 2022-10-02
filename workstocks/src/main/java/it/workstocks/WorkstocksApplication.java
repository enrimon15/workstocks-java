package it.workstocks;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WorkstocksApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(WorkstocksApplication.class, args);
	}
	
}
