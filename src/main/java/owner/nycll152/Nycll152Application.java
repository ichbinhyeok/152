package owner.nycll152;

import owner.nycll152.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Nycll152Application {

	public static void main(String[] args) {
		SpringApplication.run(Nycll152Application.class, args);
	}

}
