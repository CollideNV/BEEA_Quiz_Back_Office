package be.collide.quizbackoffice.resource;

import be.collide.quizbackoffice.domain.Quiz;
import be.collide.quizbackoffice.service.QuizService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.UUID;

@Path("/quizzes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuizResource {
    @Inject
    QuizService service;

    @GET
    public List<Quiz> getAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Quiz getSingle(@PathParam("id") UUID id) {
        return service.get(id);
    }

    @POST
    public Response add(Quiz quiz, @Context UriInfo uriInfo) {
        service.create(quiz);

        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(quiz.getId().toString());
        return Response.created(uriBuilder.build()).build();
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") UUID id, Quiz quiz) {
        service.update(id, quiz);
    }
}
