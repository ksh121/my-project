package dev.mvc.band;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"dev.mvc"}) // ★★★★★★ 패키지 주의 ★★★★★★ 
public class BandApplication {

	public static void main(String[] args) {
		SpringApplication.run(BandApplication.class, args);
	}

}
