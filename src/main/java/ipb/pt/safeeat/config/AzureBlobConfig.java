package ipb.pt.safeeat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("azure.blob")
public class AzureBlobConfig {
    private String url;
    private String container;
}
