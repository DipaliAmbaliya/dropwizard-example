package dw.example.api.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class HelloRequest {
	public static final String HELLO_PATH="hello";
	
	@JsonProperty
	@NotEmpty(message="name is required")
	private String name;
	
	@JsonProperty
	private String lang;
	
}
