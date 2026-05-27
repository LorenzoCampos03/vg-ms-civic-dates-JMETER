package pe.edu.vallegrande.sigei.civicDates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CivicDatesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CivicDatesApplication.class, args);
	}

}
