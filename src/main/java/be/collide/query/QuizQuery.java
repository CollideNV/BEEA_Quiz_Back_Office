package be.collide.query;

import lombok.Data;
import lombok.NonNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/quiz")
public class QuizQuery {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<QuizView> allQuizes() {
        return new ArrayList<>(List.of(new QuizView("Quiz Een"), new QuizView("Quiz Twee")));
    }


    @Data
    static class QuizView {
        @NonNull
        private String id;
    }
}
