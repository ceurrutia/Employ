package com.social.employ;

import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class SmokeTest extends TestBase {
    @Autowired
    private ApplicationContext context;

    @Test
    void checkBeans() {
        assertNotNull(context);
        System.out.println("El contexto de Spring cargó exitosamente");
    }

}
