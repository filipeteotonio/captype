package br.ironspark.captype;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootApplication
public class CaptypeApplication {

	public static void main(String[] args) throws JsonProcessingException {
		CaptypeRenderer.render("br.ironspark.model");
	}

}
