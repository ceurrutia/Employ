package com.social.employ;

import io.github.cdimascio.dotenv.Dotenv;

public class TestBase {
    static {
        //ejecuta ANTES que cualquier cosa de Spring, carga las variables
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
            });
            System.out.println("✅ Variables del .env cargadas en el sistema");
        } catch (Exception e) {
            System.err.println("❌ Error cargando .env: " + e.getMessage());
        }
    }
}
