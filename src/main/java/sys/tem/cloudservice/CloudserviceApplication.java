package sys.tem.cloudservice;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class CloudserviceApplication {

    public static final Marker MI = MarkerManager.getMarker("MI");

    public static void main(String[] args) {
        SpringApplication.run(CloudserviceApplication.class, args);
        log.info(MI, "---Start service---");
    }

}
