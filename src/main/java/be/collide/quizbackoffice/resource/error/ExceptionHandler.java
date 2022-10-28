package be.collide.quizbackoffice.resource.error;

import be.collide.quizbackoffice.exception.CustomException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<CustomException> {
    @Override
    public Response toResponse(CustomException e) {
        return Response
                .status(e.getHttpStatusCode())
                .entity(new ErrorMessage(e.getHttpStatusCode(), e.getMessage()))
                .build();
    }
}
