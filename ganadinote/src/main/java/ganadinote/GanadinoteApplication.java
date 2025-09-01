package ganadinote;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GanadinoteApplication {

	public static void main(String[] args) {
		// Bouncy Castle 프로바이더를 등록합니다.
        Security.addProvider(new BouncyCastleProvider());
		SpringApplication.run(GanadinoteApplication.class, args);
	}

}
