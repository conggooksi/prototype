package com.secondwind.prototype;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootTest
class DedenjjiApplicationTests {

	@Test
	@DisplayName("jwt용 난수 생성기")
	void contextLoads() {
		int keyLength = 32; // 예: 256비트

		// 난수 생성기 초기화
		SecureRandom secureRandom = new SecureRandom();
		byte[] keyBytes = new byte[keyLength];
		secureRandom.nextBytes(keyBytes);

		// 비밀 키를 Base64 인코딩하여 출력
		String base64Key = Base64.getEncoder().encodeToString(keyBytes);
		System.out.println("Generated JWT Key: " + base64Key);
	}

}
