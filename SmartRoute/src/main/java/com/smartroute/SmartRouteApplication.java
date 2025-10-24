package com.smartroute;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.smartroute.presentation.SmartRouteTerminal;

@SpringBootApplication
public class SmartRouteApplication implements CommandLineRunner {

	private final SmartRouteTerminal terminal;

	public SmartRouteApplication(SmartRouteTerminal terminal) {
		this.terminal = terminal;
	}

	public static void main(String[] args) {
		SpringApplication.run(SmartRouteApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		terminal.iniciar();
	}
}