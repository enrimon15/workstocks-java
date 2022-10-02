package it.workstocks.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Component
@ConfigurationProperties(prefix = "workstocks")
public class WorkstocksProperties {
	
	private String brokerUrl;
	
	private String dateFormat;
	
	private String queueName;

	@NestedConfigurationProperty
	private Site site;
	
	@NestedConfigurationProperty
	private ResetPassword resetPassword;
	
	@Getter
	@Setter
	public static class Site {
		private String protocol;
		private String host;
		private String port;
		private String url;
	}
	
	@Getter
	@Setter
	public static class ResetPassword {
		private String tokenValidity;
	}
}
