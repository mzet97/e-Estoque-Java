package io.github.mzet97.eestoque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.modulith.Modulithic;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@Modulithic
public class EEstoqueApplication {

    public static void main(String[] args) {
        SpringApplication.run(EEstoqueApplication.class, args);
    }
}
