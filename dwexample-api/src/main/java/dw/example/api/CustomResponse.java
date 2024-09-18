package dw.example.api;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("helloResponse")
public class CustomResponse {

	@JsonProperty("status")
	private CustomResponseStatus customResponseStatus;

	@JsonProperty
	Map<String,String> greetings;
	
	public CustomResponse() {
		//Default constructor is needed by Jackson for unmarshaling
	}
	
	public CustomResponse(CustomResponseStatus customResponseStatus) {
		this.customResponseStatus = customResponseStatus;
	}

	public CustomResponseStatus getHelloResponseStatus() {
		return customResponseStatus;
	}

	public void setHelloResponseStatus(CustomResponseStatus customResponseStatus) {
		this.customResponseStatus = customResponseStatus;
	}

	public Map<String, String> getGreetings() {
		return greetings;
	}

	public void setGreetings(Map<String, String> greetings) {
		this.greetings = greetings;
	}
	
	

}
