package task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import task.service.AsynchronousService;

@Slf4j
@SpringBootApplication
public class Main implements CommandLineRunner {

  @Autowired
  private AsynchronousService asynchronousService;

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Override
  public void run(String... args) {
    asynchronousService.executeAsynchronously();
    log.info("Application Started");

  }
}
