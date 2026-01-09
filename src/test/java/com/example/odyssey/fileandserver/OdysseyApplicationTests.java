package com.example.odyssey.fileandserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"efm.enabled=false"})
class OdysseyApplicationTests {

	@Test
	void contextLoads() {
	}

}
