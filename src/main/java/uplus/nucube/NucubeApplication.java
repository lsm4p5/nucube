package uplus.nucube;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@Slf4j
@EnableJpaAuditing
public class NucubeApplication {

	public static void main(String[] args) {

		SpringApplication.run(NucubeApplication.class, args);
		printMain();

	}

	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module();
//		hibernate5Module.configure( Hibernate5Module.Feature.FORCE_LAZY_LOADING, true );
		return hibernate5Module;
	}

	public static void printMain() {

		String mainPicture = null;

		mainPicture = "\n====================start=======================" +
				"                                                 \n" +
				"                                                 \n" +
				"                                                 \n" +
				"                  Main + 시작                     \n" +
				"                                                 \n" +
				"                                                 \n" +
				"                                                 \n" +
				"=====================end=======================";
		log.info( mainPicture );
	}


}
