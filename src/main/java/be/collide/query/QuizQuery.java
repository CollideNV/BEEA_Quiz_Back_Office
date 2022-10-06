package be.collide.query;

import lombok.Data;
import lombok.NonNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/quiz")
public class QuizQuery {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<QuizView> allQuizes() {
        return List.of(new QuizView("Quiz Een"), new QuizView("Quiz Twee"));
    }


    @Data
    private class QuizView {
        @NonNull
        String id;
    }
}
