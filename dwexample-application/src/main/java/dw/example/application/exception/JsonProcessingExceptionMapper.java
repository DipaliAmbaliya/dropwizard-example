package dw.example.application.exception;

import java.time.LocalDateTime;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import dw.example.api.CustomResponse;
import dw.example.api.CustomResponseStatus;

//Return our response object in case of exception
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException>{
	private static final Logger log = LoggerFactory.getLogger(JsonProcessingExceptionMapper.class);
	
	@Override
	public Response toResponse(JsonProcessingException exception) {
		log.error("Parse error",exception);
		return Response
				.status(Response.Status.BAD_REQUEST)
				.entity(new CustomResponse(new CustomResponseStatus(1,"Parse error:"+exception.getMessage(),LocalDateTime.now())))
				.build();
	}


	

}
