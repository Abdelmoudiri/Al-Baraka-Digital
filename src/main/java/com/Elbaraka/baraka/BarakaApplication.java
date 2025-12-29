package com.Elbaraka.baraka;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Base64;

@SpringBootApplication
public class BarakaApplication {

	public static void main(String[] args) {

	byte[] key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256).getEncoded();
				System.out.println(Base64.getEncoder().encodeToString(key));

		SpringApplication.run(BarakaApplication.class, args);
	}

}
