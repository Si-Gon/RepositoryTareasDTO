package tareas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PUNTO DE ENTRADA — TareasApplication
 *
 * @SpringBootApplication equivale a tres anotaciones en una:
 * - @Configuration       → esta clase puede definir beans de Spring
 * - @EnableAutoConfiguration → Spring configura automáticamente lo que detecta
 * - @ComponentScan       → escanea este paquete y subpaquetes buscando
 *                          @Controller, @Service, @Repository, etc.
 *
 * CAMBIO vs tu versión:
 * - El nombre del paquete raíz es minúsculas "tareas" (convención Java).
 *   Los paquetes SIEMPRE van en minúsculas. Solo las clases usan PascalCase.
 * - No hay cambios funcionales aquí, pero la estructura de paquetes sí cambió
 *   (de "Tareas.Controller" a "tareas.controller").
 */
@SpringBootApplication
public class TareasApplication {

    public static void main(String[] args) {
        SpringApplication.run(TareasApplication.class, args);
    }
}
