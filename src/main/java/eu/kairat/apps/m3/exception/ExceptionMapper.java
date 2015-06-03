package eu.kairat.apps.m3.exception;

import eu.kairat.apps.m3.tools.json.GsonFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
 
	public Response toResponse(Exception e) {

		String message;
		
		if(e instanceof SpecialException) {
			message = ((SpecialException)e).getExceptionCode().name();
		}
		else {
	        e.printStackTrace();
			//message = ExceptionUtils.getRootCause(e).getMessage();
	        message = "SERVER_ERROR";
		}

        // return the last message as json
        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(GsonFactory.GSON.toJson(message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}