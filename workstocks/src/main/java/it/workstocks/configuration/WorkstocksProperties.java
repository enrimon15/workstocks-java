package it.workstocks.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "workstocks")
public class WorkstocksProperties {
	
	private String brokerUrl;
	
	private String queueName;
	
	private String adminApiKey;
	
	private String resetPasswordValidity;
	
	private String siteUrl;
}
