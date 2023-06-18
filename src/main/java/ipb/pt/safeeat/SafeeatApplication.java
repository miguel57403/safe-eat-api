package ipb.pt.safeeat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SafeeatApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(SafeeatApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
