package fr.cs.ikats.temporaldata.exception;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ParamException.PathParamException;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handle exception and set BAD_REQUEST to the responses
 */
@Provider
public class ApplicationExceptionHandler implements ExceptionMapper<Exception> {

    private static Logger logger = Logger.getLogger(ApplicationExceptionHandler.class);


    @Override
    public Response toResponse(Exception exception) {

        if (PathParamException.class.isAssignableFrom(exception.getClass())) {
            logger.error("BAD_REQUEST ", exception);
            return Response.status(Status.BAD_REQUEST).build();
        } else if (NotFoundException.class.isAssignableFrom(exception.getClass())) {
            logger.error("NOT_FOUND " + exception.getMessage());
            return Response.status(Status.NOT_FOUND).entity(exception.getMessage()).build();
        } else if (NotAllowedException.class.isAssignableFrom(exception.getClass())) {
            logger.error("METHOD_NOT_ALLOWED ", exception);
            return Response.status(Status.METHOD_NOT_ALLOWED).entity(exception.getMessage()).build();
        } else if (WebApplicationException.class.isAssignableFrom(exception.getClass())) {
            logger.error("Error handled while processing request ", exception);
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(exception.getMessage()).build();
        } else {
            logger.error("Error handled while processing request ", exception);
            return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }
}
