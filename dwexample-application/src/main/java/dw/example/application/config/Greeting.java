package dw.example.application.config;

import javax.validation.constraints.Pattern;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Greeting {
	@JsonProperty
	@NotEmpty
	private String lang;
	
	@JsonProperty
	@Pattern(regexp = ".*%s.*", message="The pattern must contains at least one '%s' for the name")
	private String pattern;

}
