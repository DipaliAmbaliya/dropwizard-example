package dw.example.application.exception;

import java.time.LocalDateTime;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dw.example.api.CustomResponse;
import dw.example.api.CustomResponseStatus;

//Return our response object in case of exception
public class WebApplicationExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<WebApplicationException>{
	private static final Logger log = LoggerFactory.getLogger(WebApplicationExceptionMapper.class);

	@Override
	public Response toResponse(WebApplicationException exception) {
		log.error("WebApplicationException",exception);
		return Response
				.status(exception.getResponse().getStatus())
				.entity(new CustomResponse(new CustomResponseStatus(9,exception.getMessage(),LocalDateTime.now())))
				.build();
	}
}
