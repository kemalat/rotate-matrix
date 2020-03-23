package task.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("config")
@Getter
@Setter
public class ConfigProperties {

  public String inputDirectory;
  public long delayInMilis;



}
