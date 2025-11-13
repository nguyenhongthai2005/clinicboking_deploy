package com.nano.clinicbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ClinicBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicBookingApplication.class, args);
	}

}
