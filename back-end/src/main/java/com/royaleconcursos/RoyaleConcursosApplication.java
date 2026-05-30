// ARQUIVO PRINCIPAL | INICIA O SERVIDOR, SOBE A APLICACAO

package com.royaleconcursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoyaleConcursosApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoyaleConcursosApplication.class, args);
		
	}

}
