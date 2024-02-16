package be.collide.quizbackoffice.resource.error;

import be.collide.quizbackoffice.exception.ResourceNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import software.amazon.awssdk.http.HttpStatusCode;

@Provider
public class ExceptionHandler implements ExceptionMapper<ResourceNotFoundException> {
    @Override
    public Response toResponse(ResourceNotFoundException e) {
        return Response
                .status(HttpStatusCode.NOT_FOUND)
                .entity(new ErrorMessage(HttpStatusCode.NOT_FOUND, e.getMessage()))
                .build();
    }
}
