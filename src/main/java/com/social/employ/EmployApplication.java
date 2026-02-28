package com.social.employ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class EmployApplication {

	public static void main(String[] args) {
		loadEnv();
		SpringApplication.run(EmployApplication.class, args);
	}

	private static void loadEnv() {
		try {
			if (Files.exists(Paths.get(".env"))) {
				Files.lines(Paths.get(".env")).forEach(line -> {
					if (!line.trim().isEmpty() && !line.startsWith("#")) {
						String[] parts = line.split("=", 2);
						if (parts.length == 2) {
							System.setProperty(parts[0].trim(), parts[1].trim());
						}
					}
				});
			}
		} catch (IOException e) {
			System.err.println("Aviso: Error al leer el archivo .env.");
		}
	}
}