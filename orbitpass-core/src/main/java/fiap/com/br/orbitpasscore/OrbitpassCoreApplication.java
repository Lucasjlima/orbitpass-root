package fiap.com.br.orbitpasscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OrbitpassCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrbitpassCoreApplication.class, args);
    }

}
