package br.ironspark.captype;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CaptypeApplication {

	public static void main(String[] args) throws Exception {
		CaptypeRenderer.render("br.ironspark.model", "");
	}

}
