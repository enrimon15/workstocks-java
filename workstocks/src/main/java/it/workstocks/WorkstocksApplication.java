package it.workstocks;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkstocksApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(WorkstocksApplication.class, args);
	}
	
}
