package dw.example.client;

import javax.validation.Valid;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.client.JerseyClientConfiguration;
@Data
public class ServiceClientConfiguration {
	
	@Valid
	@JsonProperty("jerseyClient")
	private JerseyClientConfiguration jerseyClientConfiguration;
	
	@NotEmpty
	@JsonProperty("url")
	private String url;
	
	@JsonProperty("usename")
	private String username;
	
	@JsonProperty("password")
	private String password;

}
